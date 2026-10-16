package com.jong.mikumikureservationbackend.service.maker;

import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.maker.MakerVO;

import java.util.List;

public interface MakerService {

    List<MakerVO> getAll(Long userId);

    MakerVO create(Long userId, MakerVO vo);

    void update(Long userId, Long makerId, MakerVO vo);

    /* 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long makerId);

    UsageResponse getUsage(Long userId, Long makerId);
}
