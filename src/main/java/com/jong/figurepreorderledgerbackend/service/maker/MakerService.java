package com.jong.figurepreorderledgerbackend.service.maker;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerService {

    private final MakerMapper makerMapper;

    public List<MakerResponse> getAll(Long userId) {
        return makerMapper.selectAll(userId);
    }

    @Transactional
    public MakerResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            makerMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return makerMapper.selectByName(userId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        MakerResponse maker = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            makerMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long id) {
        MakerResponse maker = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(maker.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = makerMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        makerMapper.moveUsage(userId, id, fallbackId);
        makerMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(makerMapper.countUsage(userId, id));
    }

    private MakerResponse find(Long userId, Long id) {
        MakerResponse maker = makerMapper.selectById(userId, id);
        if (maker == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return maker;
    }
}
