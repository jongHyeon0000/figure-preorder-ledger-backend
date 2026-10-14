package com.jong.figurepreorderledgerbackend.service.type.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeService;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureTypeServiceImpl implements FigureTypeService {

    private final FigureTypeMapper figureTypeMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<FigureTypeVO> getAll(Long userId) {
        List<FigureTypeVO> list = figureTypeMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        list.sort(Comparator.comparing(v -> CategoryConstant.DEFAULT_NAME.equals(v.getName())));
        return list;
    }

    @Override
    @Transactional
    public FigureTypeVO create(Long userId, FigureTypeVO vo) {
        Long figureTypeGroupId = vo.getFigureTypeGroupId();
        if (figureTypeGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "figureTypeGroupId: 필수 값입니다");
        }
        if (figureTypeGroupMapper.selectById(userId, figureTypeGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            figureTypeMapper.insert(userId, figureTypeGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return figureTypeMapper.selectByParentAndName(userId, figureTypeGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long figureTypeId, FigureTypeVO vo) {
        FigureTypeVO child = find(userId, figureTypeId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureTypeMapper.update(userId, figureTypeId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long figureTypeId) {
        FigureTypeVO child = find(userId, figureTypeId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (figureTypeMapper.countByParent(userId, child.getFigureTypeGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = figureTypeMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        figureTypeMapper.moveUsage(userId, figureTypeId, fallbackId);
        figureTypeMapper.delete(userId, figureTypeId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.FIGURE_TYPE, Collections.singletonList(figureTypeId));
    }

    @Override
    public UsageResponse getUsage(Long userId, Long figureTypeId) {
        find(userId, figureTypeId);
        return new UsageResponse(figureTypeMapper.countUsage(userId, figureTypeId));
    }

    private FigureTypeVO find(Long userId, Long figureTypeId) {
        FigureTypeVO child = figureTypeMapper.selectById(userId, figureTypeId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
