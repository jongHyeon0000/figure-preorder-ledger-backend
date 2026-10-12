package com.jong.figurepreorderledgerbackend.service.series;

import com.jong.figurepreorderledgerbackend.vo.series.response.SeriesGroupResponse;
import com.jong.figurepreorderledgerbackend.vo.common.UsageResponse;

import java.util.List;

public interface SeriesGroupService {

    List<SeriesGroupResponse> getTree(Long userId);

    /* 상위를 만들면 기본 하위(기타)도 같이 만든다 */
    SeriesGroupResponse create(Long userId, String name);

    void update(Long userId, Long seriesGroupId, String name);

    /* 쓰던 상품을 기본 항목(기타 > 기타)으로 옮긴 뒤 삭제한다. 하위는 DB에서 함께 삭제된다 */
    void delete(Long userId, Long seriesGroupId);

    UsageResponse getUsage(Long userId, Long seriesGroupId);
}
