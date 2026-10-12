package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopVO;

import java.util.List;

public interface ShopService {

    List<ShopVO> getAll(Long userId);

    ShopVO create(Long userId, ShopVO vo);

    void update(Long userId, Long shopId, ShopVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long shopId);

    UsageResponse getUsage(Long userId, Long shopId);
}
