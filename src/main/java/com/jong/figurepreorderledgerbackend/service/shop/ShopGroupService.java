package com.jong.figurepreorderledgerbackend.service.shop;

import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.shop.ShopGroupVO;

import java.util.List;

public interface ShopGroupService {

    List<ShopGroupVO> getTree(Long userId);

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    ShopGroupVO create(Long userId, ShopGroupVO vo);

    void update(Long userId, Long shopGroupId, ShopGroupVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    void delete(Long userId, Long shopGroupId);

    UsageResponse getUsage(Long userId, Long shopGroupId);
}
