package com.jong.figurepreorderledgerbackend.service.genre.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterGroupService;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupVO;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
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
public class GenreCharacterGroupServiceImpl implements GenreCharacterGroupService {

    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<GenreCharacterGroupVO> getTree(Long userId) {
        List<GenreCharacterGroupVO> parents = genreCharacterGroupMapper.selectAll(userId);
        List<GenreCharacterVO> children = genreCharacterMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        parents.sort(Comparator.comparing(p -> CategoryConstant.DEFAULT_NAME.equals(p.getName())));
        children.sort(Comparator.comparing(c -> CategoryConstant.DEFAULT_NAME.equals(c.getName())));
        for (GenreCharacterGroupVO parent : parents) {
            List<GenreCharacterVO> own = new ArrayList<>();
            for (GenreCharacterVO child : children) {
                if (parent.getId().equals(child.getGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    @Override
    @Transactional
    public GenreCharacterGroupVO create(Long userId, GenreCharacterGroupVO vo) {
        String trimmed = vo.getName().trim();
        try {
            genreCharacterGroupMapper.insert(userId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        GenreCharacterGroupVO parent = genreCharacterGroupMapper.selectByName(userId, trimmed);

        genreCharacterMapper.insert(userId, parent.getId(), CategoryConstant.DEFAULT_NAME, null);
        List<GenreCharacterVO> children = new ArrayList<>();
        children.add(genreCharacterMapper.selectByParentAndName(userId, parent.getId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long genreCharacterGroupId, GenreCharacterGroupVO vo) {
        GenreCharacterGroupVO parent = find(userId, genreCharacterGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterGroupMapper.update(userId, genreCharacterGroupId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long genreCharacterGroupId) {
        GenreCharacterGroupVO parent = find(userId, genreCharacterGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = genreCharacterGroupMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        genreCharacterGroupMapper.moveInsertFallback(userId, genreCharacterGroupId, fallbackId);
        genreCharacterGroupMapper.moveDeleteLinks(userId, genreCharacterGroupId);
        List<Long> childIds = genreCharacterMapper.selectIdsByParent(userId, genreCharacterGroupId);   // 상위를 지우면 하위도 CASCADE로 지워지므로 미리 모아 둔다
        genreCharacterGroupMapper.delete(userId, genreCharacterGroupId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.GENRE_CHARACTER_GROUP, Collections.singletonList(genreCharacterGroupId));
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.GENRE_CHARACTER, childIds);
    }

    @Override
    public UsageResponse getUsage(Long userId, Long genreCharacterGroupId) {
        find(userId, genreCharacterGroupId);
        return new UsageResponse(genreCharacterGroupMapper.countUsage(userId, genreCharacterGroupId));
    }

    private GenreCharacterGroupVO find(Long userId, Long genreCharacterGroupId) {
        GenreCharacterGroupVO parent = genreCharacterGroupMapper.selectById(userId, genreCharacterGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
