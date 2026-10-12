package com.jong.figurepreorderledgerbackend.service.series.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.service.series.SeriesGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupVO;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;
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
    public List<SeriesGroupVO> getTree(Long userId) {
        List<SeriesGroupVO> parents = seriesGroupMapper.selectAll(userId);
        List<SeriesVO> children = seriesMapper.selectAll(userId);
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
            seriesGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        SeriesGroupVO parent = seriesGroupMapper.selectByName(userId, trimmed);

        seriesMapper.insert(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME);
        List<SeriesVO> children = new ArrayList<>();
        children.add(seriesMapper.selectByParentAndName(userId, parent.getSeriesGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesGroupId, SeriesGroupVO vo) {
        SeriesGroupVO parent = find(userId, seriesGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesGroupMapper.updateName(userId, seriesGroupId, vo.getName().trim());
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
        seriesGroupMapper.delete(userId, seriesGroupId);
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
