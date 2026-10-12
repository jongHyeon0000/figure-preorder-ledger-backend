package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.vo.shop.ShopResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;

import java.util.List;

public interface ShopService {

    List<ShopResponse> getAll(Long userId);

    ShopResponse create(Long userId, Long shopGroupId, String name);

    void update(Long userId, Long shopId, String name);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long shopId);

    UsageResponse getUsage(Long userId, Long shopId);
}
