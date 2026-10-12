package com.jong.figurepreorderledgerbackend.service.series;

import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;

import java.util.List;

public interface SeriesService {

    List<SeriesResponse> getAll(Long userId);

    SeriesResponse create(Long userId, Long seriesGroupId, String name);

    void update(Long userId, Long seriesId, String name);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다 */
    void delete(Long userId, Long seriesId);

    UsageResponse getUsage(Long userId, Long seriesId);
}
