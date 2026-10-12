package com.jong.figurepreorderledgerbackend.service.series.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.service.series.SeriesService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
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
    public List<SeriesVO> getAll(Long userId) {
        return seriesMapper.selectAll(userId);
    }

    @Override
    @Transactional
    public SeriesVO create(Long userId, SeriesVO vo) {
        Long seriesGroupId = vo.getSeriesGroupId();
        if (seriesGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "seriesGroupId: 필수 값입니다");
        }
        if (seriesGroupMapper.selectById(userId, seriesGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            seriesMapper.insert(userId, seriesGroupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return seriesMapper.selectByParentAndName(userId, seriesGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesId, SeriesVO vo) {
        SeriesVO child = find(userId, seriesId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesMapper.updateName(userId, seriesId, vo.getName().trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long seriesId) {
        SeriesVO child = find(userId, seriesId);
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

    private SeriesVO find(Long userId, Long seriesId) {
        SeriesVO child = seriesMapper.selectById(userId, seriesId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
