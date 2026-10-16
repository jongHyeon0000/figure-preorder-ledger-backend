package com.jong.mikumikureservationbackend.service.genre.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterMapper;
import com.jong.mikumikureservationbackend.service.genre.GenreCharacterService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;
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
public class GenreCharacterServiceImpl implements GenreCharacterService {

    private final GenreCharacterMapper genreCharacterMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public List<GenreCharacterVO> getAll(Long userId) {
        List<GenreCharacterVO> list = genreCharacterMapper.selectAll(userId);
        // 아무 카테고리에도 해당되지 않는 미분류(기타)는 항상 맨 뒤로 보낸다(안정 정렬이라 나머지는 생성 순 유지)
        list.sort(Comparator.comparing(v -> CategoryConstant.DEFAULT_NAME.equals(v.getName())));
        return list;
    }

    @Override
    @Transactional
    public GenreCharacterVO create(Long userId, GenreCharacterVO vo) {
        Long genreCharacterGroupId = vo.getGroupId();
        if (genreCharacterGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "genreCharacterGroupId: 필수 값입니다");
        }
        if (genreCharacterGroupMapper.selectById(userId, genreCharacterGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            genreCharacterMapper.insert(userId, genreCharacterGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return genreCharacterMapper.selectByParentAndName(userId, genreCharacterGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long genreCharacterId, GenreCharacterVO vo) {
        GenreCharacterVO child = find(userId, genreCharacterId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterMapper.update(userId, genreCharacterId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long genreCharacterId) {
        GenreCharacterVO child = find(userId, genreCharacterId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (genreCharacterMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = genreCharacterMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        genreCharacterMapper.moveInsertFallback(userId, genreCharacterId, fallbackId);
        genreCharacterMapper.moveDeleteLinks(userId, genreCharacterId);
        genreCharacterMapper.delete(userId, genreCharacterId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.GENRE_CHARACTER, Collections.singletonList(genreCharacterId));
    }

    @Override
    public UsageResponse getUsage(Long userId, Long genreCharacterId) {
        find(userId, genreCharacterId);
        return new UsageResponse(genreCharacterMapper.countUsage(userId, genreCharacterId));
    }

    private GenreCharacterVO find(Long userId, Long genreCharacterId) {
        GenreCharacterVO child = genreCharacterMapper.selectById(userId, genreCharacterId);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
