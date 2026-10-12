package com.jong.figurepreorderledgerbackend.service.type.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.service.type.FigureTypeGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureTypeGroupServiceImpl implements FigureTypeGroupService {

    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final FigureTypeMapper figureTypeMapper;

    @Override
    public List<FigureTypeGroupResponse> getTree(Long userId) {
        List<FigureTypeGroupResponse> parents = figureTypeGroupMapper.selectAll(userId);
        List<FigureTypeResponse> children = figureTypeMapper.selectAll(userId);
        for (FigureTypeGroupResponse parent : parents) {
            List<FigureTypeResponse> own = new ArrayList<>();
            for (FigureTypeResponse child : children) {
                if (parent.getFigureTypeGroupId().equals(child.getFigureTypeGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    @Override
    @Transactional
    public FigureTypeGroupResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            figureTypeGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        FigureTypeGroupResponse parent = figureTypeGroupMapper.selectByName(userId, trimmed);

        figureTypeMapper.insert(userId, parent.getFigureTypeGroupId(), CategoryConstant.DEFAULT_NAME);
        List<FigureTypeResponse> children = new ArrayList<>();
        children.add(figureTypeMapper.selectByParentAndName(userId, parent.getFigureTypeGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long figureTypeGroupId, String name) {
        FigureTypeGroupResponse parent = find(userId, figureTypeGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureTypeGroupMapper.updateName(userId, figureTypeGroupId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long figureTypeGroupId) {
        FigureTypeGroupResponse parent = find(userId, figureTypeGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = figureTypeGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        figureTypeGroupMapper.moveUsage(userId, figureTypeGroupId, fallbackId);
        figureTypeGroupMapper.delete(userId, figureTypeGroupId);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long figureTypeGroupId) {
        find(userId, figureTypeGroupId);
        return new UsageResponse(figureTypeGroupMapper.countUsage(userId, figureTypeGroupId));
    }

    private FigureTypeGroupResponse find(Long userId, Long figureTypeGroupId) {
        FigureTypeGroupResponse parent = figureTypeGroupMapper.selectById(userId, figureTypeGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
