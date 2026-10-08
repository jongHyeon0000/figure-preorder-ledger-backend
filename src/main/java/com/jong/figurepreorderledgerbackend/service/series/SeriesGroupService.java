package com.jong.figurepreorderledgerbackend.service.series;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesGroupService {

    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;

    public List<SeriesGroupResponse> getTree(Long userId) {
        List<SeriesGroupResponse> parents = seriesGroupMapper.selectAll(userId);
        List<SeriesResponse> children = seriesMapper.selectAll(userId);
        for (SeriesGroupResponse parent : parents) {
            List<SeriesResponse> own = new ArrayList<>();
            for (SeriesResponse child : children) {
                if (parent.getId().equals(child.getGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public SeriesGroupResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            seriesGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        SeriesGroupResponse parent = seriesGroupMapper.selectByName(userId, trimmed);

        seriesMapper.insert(userId, parent.getId(), CategoryConstant.DEFAULT_NAME);
        List<SeriesResponse> children = new ArrayList<>();
        children.add(seriesMapper.selectByParentAndName(userId, parent.getId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        SeriesGroupResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesGroupMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long id) {
        SeriesGroupResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = seriesGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        seriesGroupMapper.moveInsertFallback(userId, id, fallbackId);
        seriesGroupMapper.moveDeleteLinks(userId, id);
        seriesGroupMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(seriesGroupMapper.countUsage(userId, id));
    }

    private SeriesGroupResponse find(Long userId, Long id) {
        SeriesGroupResponse parent = seriesGroupMapper.selectById(userId, id);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
