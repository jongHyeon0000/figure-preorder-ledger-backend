package com.jong.figurepreorderledgerbackend.service.genre.impl;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.service.genre.GenreCharacterGroupService;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupVO;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreCharacterGroupServiceImpl implements GenreCharacterGroupService {

    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;

    @Override
    public List<GenreCharacterGroupVO> getTree(Long userId) {
        List<GenreCharacterGroupVO> parents = genreCharacterGroupMapper.selectAll(userId);
        List<GenreCharacterVO> children = genreCharacterMapper.selectAll(userId);
        for (GenreCharacterGroupVO parent : parents) {
            List<GenreCharacterVO> own = new ArrayList<>();
            for (GenreCharacterVO child : children) {
                if (parent.getGenreCharacterGroupId().equals(child.getGenreCharacterGroupId())) {
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
            genreCharacterGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        GenreCharacterGroupVO parent = genreCharacterGroupMapper.selectByName(userId, trimmed);

        genreCharacterMapper.insert(userId, parent.getGenreCharacterGroupId(), CategoryConstant.DEFAULT_NAME);
        List<GenreCharacterVO> children = new ArrayList<>();
        children.add(genreCharacterMapper.selectByParentAndName(userId, parent.getGenreCharacterGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Override
    @Transactional
    public void update(Long userId, Long genreCharacterGroupId, GenreCharacterGroupVO vo) {
        GenreCharacterGroupVO parent = find(userId, genreCharacterGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterGroupMapper.updateName(userId, genreCharacterGroupId, vo.getName().trim());
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
        genreCharacterGroupMapper.delete(userId, genreCharacterGroupId);
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
