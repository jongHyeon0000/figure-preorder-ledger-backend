package com.jong.mikumikureservationbackend.service.series.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.series.SeriesGroupMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesMapper;
import com.jong.mikumikureservationbackend.service.series.SeriesService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;
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
public class SeriesServiceImpl implements SeriesService {

    private final SeriesMapper seriesMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<SeriesVO> getAll(Long userId) {
        List<SeriesVO> list = seriesMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        list.sort(Comparator.comparing(v -> CategoryConstant.DEFAULT_NAME.equals(v.getName())));
        return list;
    }

    @Override
    @Transactional
    public SeriesVO create(Long userId, SeriesVO vo) {
        Long seriesGroupId = vo.getGroupId();
        if (seriesGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "seriesGroupId: 필수 값입니다");
        }
        if (seriesGroupMapper.selectById(userId, seriesGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            seriesMapper.insert(userId, seriesGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return seriesMapper.selectByParentAndName(userId, seriesGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesId, SeriesVO vo) {
        SeriesVO child = find(userId, seriesId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesMapper.update(userId, seriesId, vo.getName().trim(), vo.getDescription());
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
        if (seriesMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = seriesMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        seriesMapper.moveInsertFallback(userId, seriesId, fallbackId);
        seriesMapper.moveDeleteLinks(userId, seriesId);
        seriesMapper.delete(userId, seriesId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SERIES, Collections.singletonList(seriesId));
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
