package com.jong.figurepreorderledgerbackend.service.genre;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreMapper;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreCharacterService {

    private final GenreCharacterMapper genreCharacterMapper;
    private final GenreMapper genreMapper;

    public List<GenreCharacterResponse> getAll(Long userId) {
        return genreCharacterMapper.selectAll(userId);
    }

    @Transactional
    public GenreCharacterResponse create(Long userId, Long genreId, String name) {
        if (genreMapper.selectById(userId, genreId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        String trimmed = name.trim();
        try {
            genreCharacterMapper.insert(userId, genreId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        return genreCharacterMapper.selectByParentAndName(userId, genreId, trimmed);
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        GenreCharacterResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    @Transactional
    public void delete(Long userId, Long id) {
        GenreCharacterResponse child = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (genreCharacterMapper.countByParent(userId, child.getGenreId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }
        Long fallbackId = genreCharacterMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        genreCharacterMapper.moveInsertFallback(userId, id, fallbackId);
        genreCharacterMapper.moveDeleteLinks(userId, id);
        genreCharacterMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(genreCharacterMapper.countUsage(userId, id));
    }

    private GenreCharacterResponse find(Long userId, Long id) {
        GenreCharacterResponse child = genreCharacterMapper.selectById(userId, id);
        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return child;
    }
}
