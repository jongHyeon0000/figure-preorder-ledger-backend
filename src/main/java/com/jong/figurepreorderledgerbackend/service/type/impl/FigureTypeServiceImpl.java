package com.jong.figurepreorderledgerbackend.service.type.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureTypeServiceImpl implements FigureTypeService {

    private final FigureTypeMapper figureTypeMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;

    @Override
    public List<FigureTypeResponse> getAll(Long userId) {
        return figureTypeMapper.selectAll(userId);
    }

    @Override
    @Transactional
    public FigureTypeResponse create(Long userId, Long figureTypeGroupId, String name) {
        if (figureTypeGroupMapper.selectById(userId, figureTypeGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            figureTypeMapper.insert(userId, figureTypeGroupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return figureTypeMapper.selectByParentAndName(userId, figureTypeGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long figureTypeId, String name) {
        FigureTypeResponse child = find(userId, figureTypeId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureTypeMapper.updateName(userId, figureTypeId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long figureTypeId) {
        FigureTypeResponse child = find(userId, figureTypeId);
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
    }

    @Override
    public UsageResponse getUsage(Long userId, Long figureTypeId) {
        find(userId, figureTypeId);
        return new UsageResponse(figureTypeMapper.countUsage(userId, figureTypeId));
    }

    private FigureTypeResponse find(Long userId, Long figureTypeId) {
        FigureTypeResponse child = figureTypeMapper.selectById(userId, figureTypeId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
