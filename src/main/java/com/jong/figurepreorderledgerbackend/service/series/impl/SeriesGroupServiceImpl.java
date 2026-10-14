package com.jong.figurepreorderledgerbackend.service.series.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesGroupServiceImpl implements SeriesGroupService {

    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<SeriesGroupVO> getTree(Long userId) {
        List<SeriesGroupVO> parents = seriesGroupMapper.selectAll(userId);
        List<SeriesVO> children = seriesMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        parents.sort(Comparator.comparing(p -> CategoryConstant.DEFAULT_NAME.equals(p.getName())));
        children.sort(Comparator.comparing(c -> CategoryConstant.DEFAULT_NAME.equals(c.getName())));
        for (SeriesGroupVO parent : parents) {
            List<SeriesVO> own = new ArrayList<>();
            for (SeriesVO child : children) {
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
    public SeriesGroupVO create(Long userId, SeriesGroupVO vo) {
        String trimmed = vo.getName().trim();
        try {
            seriesGroupMapper.insert(userId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        SeriesGroupVO parent = seriesGroupMapper.selectByName(userId, trimmed);

        seriesMapper.insert(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME, null);
        List<SeriesVO> children = new ArrayList<>();
        children.add(seriesMapper.selectByParentAndName(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesGroupId, SeriesGroupVO vo) {
        SeriesGroupVO parent = find(userId, seriesGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesGroupMapper.update(userId, seriesGroupId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long seriesGroupId) {
        SeriesGroupVO parent = find(userId, seriesGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = seriesGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        seriesGroupMapper.moveInsertFallback(userId, seriesGroupId, fallbackId);
        seriesGroupMapper.moveDeleteLinks(userId, seriesGroupId);
        List<Long> childIds = seriesMapper.selectIdsByParent(userId, seriesGroupId);   // 상위를 지우면 하위도 CASCADE로 지워지므로 미리 모아 둔다
        seriesGroupMapper.delete(userId, seriesGroupId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SERIES_GROUP, Collections.singletonList(seriesGroupId));
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SERIES, childIds);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long seriesGroupId) {
        find(userId, seriesGroupId);
        return new UsageResponse(seriesGroupMapper.countUsage(userId, seriesGroupId));
    }

    private SeriesGroupVO find(Long userId, Long seriesGroupId) {
        SeriesGroupVO parent = seriesGroupMapper.selectById(userId, seriesGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
