package com.jong.figurepreorderledgerbackend.service.genre;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreMapper;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreResponse;
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
public class GenreService {

    private final GenreMapper genreMapper;
    private final GenreCharacterMapper genreCharacterMapper;

    public List<GenreResponse> getTree(Long userId) {
        List<GenreResponse> parents = genreMapper.selectAll(userId);
        List<GenreCharacterResponse> children = genreCharacterMapper.selectAll(userId);
        for (GenreResponse parent : parents) {
            List<GenreCharacterResponse> own = new ArrayList<>();
            for (GenreCharacterResponse child : children) {
                if (parent.getId().equals(child.getGenreId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public GenreResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            genreMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        GenreResponse parent = genreMapper.selectByName(userId, trimmed);

        genreCharacterMapper.insert(userId, parent.getId(), CategoryConstant.DEFAULT_NAME);
        List<GenreCharacterResponse> children = new ArrayList<>();
        children.add(genreCharacterMapper.selectByParentAndName(userId, parent.getId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long id, String name) {
        GenreResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreMapper.updateName(userId, id, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long id) {
        GenreResponse parent = find(userId, id);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        Long fallbackId = genreMapper.selectDefaultChildId(userId, CategoryConstant.DEFAULT_NAME);
        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        genreMapper.moveInsertFallback(userId, id, fallbackId);
        genreMapper.moveDeleteLinks(userId, id);
        genreMapper.delete(userId, id);
    }

    public UsageResponse getUsage(Long userId, Long id) {
        find(userId, id);
        return new UsageResponse(genreMapper.countUsage(userId, id));
    }

    private GenreResponse find(Long userId, Long id) {
        GenreResponse parent = genreMapper.selectById(userId, id);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
