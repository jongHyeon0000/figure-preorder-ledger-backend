package com.jong.figurepreorderledgerbackend.service.maker;

import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.maker.MakerResponse;

import java.util.List;

public interface MakerService {

    List<MakerResponse> getAll(Long userId);

    MakerResponse create(Long userId, String name);

    void update(Long userId, Long makerId, String name);

    /* 쓰던 상품을 기본 항목(기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long makerId);

    UsageResponse getUsage(Long userId, Long makerId);
}
