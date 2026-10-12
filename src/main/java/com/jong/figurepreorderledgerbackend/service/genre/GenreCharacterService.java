package com.jong.figurepreorderledgerbackend.service.genre;

import com.jong.figurepreorderledgerbackend.vo.genre.GenreCharacterResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;

import java.util.List;

public interface GenreCharacterService {

    List<GenreCharacterResponse> getAll(Long userId);

    GenreCharacterResponse create(Long userId, Long genreCharacterGroupId, String name);

    void update(Long userId, Long genreCharacterId, String name);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long genreCharacterId);

    UsageResponse getUsage(Long userId, Long genreCharacterId);
}
