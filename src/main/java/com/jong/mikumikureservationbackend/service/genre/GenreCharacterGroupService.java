package com.jong.mikumikureservationbackend.service.genre;

import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterGroupVO;

import java.util.List;

public interface GenreCharacterGroupService {

    List<GenreCharacterGroupVO> getTree(Long userId);

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    GenreCharacterGroupVO create(Long userId, GenreCharacterGroupVO vo);

    void update(Long userId, Long genreCharacterGroupId, GenreCharacterGroupVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    void delete(Long userId, Long genreCharacterGroupId);

    UsageResponse getUsage(Long userId, Long genreCharacterGroupId);
}
