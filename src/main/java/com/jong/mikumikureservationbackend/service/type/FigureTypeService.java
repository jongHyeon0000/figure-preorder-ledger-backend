package com.jong.mikumikureservationbackend.service.type;

import com.jong.mikumikureservationbackend.vo.common.UsageResponse;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeVO;

import java.util.List;

public interface FigureTypeService {

    List<FigureTypeVO> getAll(Long userId);

    FigureTypeVO create(Long userId, FigureTypeVO vo);

    void update(Long userId, Long figureTypeId, FigureTypeVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long figureTypeId);

    UsageResponse getUsage(Long userId, Long figureTypeId);
}
