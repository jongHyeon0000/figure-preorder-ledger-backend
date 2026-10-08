package com.jong.figurepreorderledgerbackend.service.series;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesService {

    private final SeriesMapper seriesMapper;
    private final SeriesGroupMapper seriesGroupMapper;

    public List<SeriesResponse> getAll(Long userId) {
        return seriesMapper.selectAll(userId);
    }

    @Transactional
    public SeriesResponse create(Long userId, Long groupId, String name) {
        if (seriesGroupMapper.selectById(userId, groupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            seriesMapper.insert(userId, groupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return seriesMapper.selectByParentAndName(userId, groupId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        SeriesResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long id) {
        SeriesResponse child = find(userId, id);
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
        seriesMapper.moveInsertFallback(userId, id, fallbackId);
        seriesMapper.moveDeleteLinks(userId, id);
        seriesMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(seriesMapper.countUsage(userId, id));
    }

    private SeriesResponse find(Long userId, Long id) {
        SeriesResponse child = seriesMapper.selectById(userId, id);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
