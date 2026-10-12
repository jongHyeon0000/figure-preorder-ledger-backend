package com.jong.figurepreorderledgerbackend.service.genre.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreCharacterServiceImpl implements GenreCharacterService {

    private final GenreCharacterMapper genreCharacterMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;

    @Override
    public List<GenreCharacterVO> getAll(Long userId) {
        return genreCharacterMapper.selectAll(userId);
    }

    @Override
    @Transactional
    public GenreCharacterVO create(Long userId, GenreCharacterVO vo) {
        Long genreCharacterGroupId = vo.getGenreCharacterGroupId();
        if (genreCharacterGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "genreCharacterGroupId: 필수 값입니다");
        }
        if (genreCharacterGroupMapper.selectById(userId, genreCharacterGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = vo.getName().trim();
        try {
            genreCharacterMapper.insert(userId, genreCharacterGroupId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return genreCharacterMapper.selectByParentAndName(userId, genreCharacterGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long genreCharacterId, GenreCharacterVO vo) {
        GenreCharacterVO child = find(userId, genreCharacterId);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterMapper.updateName(userId, genreCharacterId, vo.getName().trim());
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
        if (genreCharacterMapper.countByParent(userId, child.getGenreCharacterGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = genreCharacterMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        genreCharacterMapper.moveInsertFallback(userId, genreCharacterId, fallbackId);
        genreCharacterMapper.moveDeleteLinks(userId, genreCharacterId);
        genreCharacterMapper.delete(userId, genreCharacterId);
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
