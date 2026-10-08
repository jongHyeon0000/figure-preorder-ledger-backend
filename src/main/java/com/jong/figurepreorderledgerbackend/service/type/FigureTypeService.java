package com.jong.figurepreorderledgerbackend.service.type;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureSubtypeMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.vo.type.FigureSubtypeResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureTypeService {

    private final FigureTypeMapper figureTypeMapper;
    private final FigureSubtypeMapper figureSubtypeMapper;

    public List<FigureTypeResponse> getTree(Long userId) {
        List<FigureTypeResponse> parents = figureTypeMapper.selectAll(userId);
        List<FigureSubtypeResponse> children = figureSubtypeMapper.selectAll(userId);
        for (FigureTypeResponse parent : parents) {
            List<FigureSubtypeResponse> own = new ArrayList<>();
            for (FigureSubtypeResponse child : children) {
                if (parent.getId().equals(child.getTypeId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public FigureTypeResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            figureTypeMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        FigureTypeResponse parent = figureTypeMapper.selectByName(userId, trimmed);

        figureSubtypeMapper.insert(userId, parent.getId(), CategoryConstant.DEFAULT_NAME);
        List<FigureSubtypeResponse> children = new ArrayList<>();
        children.add(figureSubtypeMapper.selectByParentAndName(userId, parent.getId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        FigureTypeResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureTypeMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long id) {
        FigureTypeResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = figureTypeMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        figureTypeMapper.moveUsage(userId, id, fallbackId);
        figureTypeMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(figureTypeMapper.countUsage(userId, id));
    }

    private FigureTypeResponse find(Long userId, Long id) {
        FigureTypeResponse parent = figureTypeMapper.selectById(userId, id);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
