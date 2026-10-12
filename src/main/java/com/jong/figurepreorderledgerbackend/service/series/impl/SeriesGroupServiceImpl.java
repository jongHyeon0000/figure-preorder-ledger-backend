package com.jong.figurepreorderledgerbackend.service.series.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesGroupServiceImpl implements SeriesGroupService {

    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;

    @Override
    public List<SeriesGroupResponse> getTree(Long userId) {
        List<SeriesGroupResponse> parents = seriesGroupMapper.selectAll(userId);
        List<SeriesResponse> children = seriesMapper.selectAll(userId);
        for (SeriesGroupResponse parent : parents) {
            List<SeriesResponse> own = new ArrayList<>();
            for (SeriesResponse child : children) {
                if (parent.getSeriesGroupId().equals(child.getSeriesGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    @Override
    @Transactional
    public SeriesGroupResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            seriesGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        SeriesGroupResponse parent = seriesGroupMapper.selectByName(userId, trimmed);

        seriesMapper.insert(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME);
        List<SeriesResponse> children = new ArrayList<>();
        children.add(seriesMapper.selectByParentAndName(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesGroupId, String name) {
        SeriesGroupResponse parent = find(userId, seriesGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesGroupMapper.updateName(userId, seriesGroupId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long seriesGroupId) {
        SeriesGroupResponse parent = find(userId, seriesGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = seriesGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        seriesGroupMapper.moveInsertFallback(userId, seriesGroupId, fallbackId);
        seriesGroupMapper.moveDeleteLinks(userId, seriesGroupId);
        seriesGroupMapper.delete(userId, seriesGroupId);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long seriesGroupId) {
        find(userId, seriesGroupId);
        return new UsageResponse(seriesGroupMapper.countUsage(userId, seriesGroupId));
    }

    private SeriesGroupResponse find(Long userId, Long seriesGroupId) {
        SeriesGroupResponse parent = seriesGroupMapper.selectById(userId, seriesGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
