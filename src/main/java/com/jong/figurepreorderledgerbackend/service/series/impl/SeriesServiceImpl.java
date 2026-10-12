package com.jong.figurepreorderledgerbackend.service.series.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.service.series.SeriesService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesServiceImpl implements SeriesService {

    private final SeriesMapper seriesMapper;
    private final SeriesGroupMapper seriesGroupMapper;

    @Override
    public List<SeriesResponse> getAll(Long userId) {
        return seriesMapper.selectAll(userId);
    }

    @Override
    @Transactional
    public SeriesResponse create(Long userId, Long seriesGroupId, String name) {
        if (seriesGroupMapper.selectById(userId, seriesGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            seriesMapper.insert(userId, seriesGroupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return seriesMapper.selectByParentAndName(userId, seriesGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesId, String name) {
        SeriesResponse child = find(userId, seriesId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesMapper.updateName(userId, seriesId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long seriesId) {
        SeriesResponse child = find(userId, seriesId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (seriesMapper.countByParent(userId, child.getSeriesGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = seriesMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        seriesMapper.moveInsertFallback(userId, seriesId, fallbackId);
        seriesMapper.moveDeleteLinks(userId, seriesId);
        seriesMapper.delete(userId, seriesId);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long seriesId) {
        find(userId, seriesId);
        return new UsageResponse(seriesMapper.countUsage(userId, seriesId));
    }

    private SeriesResponse find(Long userId, Long seriesId) {
        SeriesResponse child = seriesMapper.selectById(userId, seriesId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
