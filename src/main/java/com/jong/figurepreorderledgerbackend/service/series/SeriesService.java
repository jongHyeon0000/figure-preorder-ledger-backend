package com.jong.figurepreorderledgerbackend.service.series;

import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;
import com.jong.figurepreorderledgerbackend.vo.series.SeriesVO;

import java.util.List;

public interface SeriesService {

    List<SeriesVO> getAll(Long userId);

    SeriesVO create(Long userId, SeriesVO vo);

    void update(Long userId, Long seriesId, SeriesVO vo);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long seriesId);

    UsageResponse getUsage(Long userId, Long seriesId);
}
