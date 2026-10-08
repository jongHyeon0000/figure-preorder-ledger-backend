package com.jong.figurepreorderledgerbackend.service.type;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureSubtypeMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureSubtypeService {

    private final FigureSubtypeMapper figureSubtypeMapper;
    private final FigureTypeMapper figureTypeMapper;

    public List<FigureSubtypeResponse> getAll(Long userId) {
        return figureSubtypeMapper.selectAll(userId);
    }

    @Transactional
    public FigureSubtypeResponse create(Long userId, Long typeId, String name) {
        if (figureTypeMapper.selectById(userId, typeId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            figureSubtypeMapper.insert(userId, typeId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return figureSubtypeMapper.selectByParentAndName(userId, typeId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        FigureSubtypeResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureSubtypeMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long id) {
        FigureSubtypeResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (figureSubtypeMapper.countByParent(userId, child.getTypeId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = figureSubtypeMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        figureSubtypeMapper.moveUsage(userId, id, fallbackId);
        figureSubtypeMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(figureSubtypeMapper.countUsage(userId, id));
    }

    private FigureSubtypeResponse find(Long userId, Long id) {
        FigureSubtypeResponse child = figureSubtypeMapper.selectById(userId, id);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
