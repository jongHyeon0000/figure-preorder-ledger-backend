package com.jong.figurepreorderledgerbackend.service.genre;

import com.jong.figurepreorderledgerbackend.common.constant.CategoryConstant;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterResponse;
import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterGroupResponse;
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
public class GenreCharacterGroupService {

    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;

    public List<GenreCharacterGroupResponse> getTree(Long userId) {
        List<GenreCharacterGroupResponse> parents = genreCharacterGroupMapper.selectAll(userId);
        List<GenreCharacterResponse> children = genreCharacterMapper.selectAll(userId);
        for (GenreCharacterGroupResponse parent : parents) {
            List<GenreCharacterResponse> own = new ArrayList<>();
            for (GenreCharacterResponse child : children) {
                if (parent.getGenreCharacterGroupId().equals(child.getGenreCharacterGroupId())) {
                    own.add(child);
                }
            }
            parent.setChildren(own);
        }
        return parents;
    }

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    @Transactional
    public GenreCharacterGroupResponse create(Long userId, String name) {
        String trimmed = name.trim();
        try {
            genreCharacterGroupMapper.insert(userId, trimmed);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
        GenreCharacterGroupResponse parent = genreCharacterGroupMapper.selectByName(userId, trimmed);

        genreCharacterMapper.insert(userId, parent.getGenreCharacterGroupId(), CategoryConstant.DEFAULT_NAME);
        List<GenreCharacterResponse> children = new ArrayList<>();
        children.add(genreCharacterMapper.selectByParentAndName(userId, parent.getGenreCharacterGroupId(), CategoryConstant.DEFAULT_NAME));
        parent.setChildren(children);
        return parent;
    }

    @Transactional
    public void update(Long userId, Long genreCharacterGroupId, String name) {
        GenreCharacterGroupResponse parent = find(userId, genreCharacterGroupId);
        if (CategoryConstant.DEFAULT_NAME.equals(parent.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterGroupMapper.updateName(userId, genreCharacterGroupId, name.trim());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    @Transactional
    public void delete(Long userId, Long genreCharacterGroupId) {
        GenreCharacterGroupResponse parent = find(userId, genreCharacterGroupId);
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

    public UsageResponse getUsage(Long userId, Long genreCharacterGroupId) {
        find(userId, genreCharacterGroupId);
        return new UsageResponse(genreCharacterGroupMapper.countUsage(userId, genreCharacterGroupId));
    }

    private GenreCharacterGroupResponse find(Long userId, Long genreCharacterGroupId) {
        GenreCharacterGroupResponse parent = genreCharacterGroupMapper.selectById(userId, genreCharacterGroupId);
        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        return parent;
    }
}
