package com.jong.figurepreorderledgerbackend.service.type;

import com.jong.figurepreorderledgerbackend.vo.type.FigureTypeResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;

import java.util.List;

public interface FigureTypeService {

    List<FigureTypeResponse> getAll(Long userId);

    FigureTypeResponse create(Long userId, Long figureTypeGroupId, String name);

    void update(Long userId, Long figureTypeId, String name);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long figureTypeId);

    UsageResponse getUsage(Long userId, Long figureTypeId);
}
