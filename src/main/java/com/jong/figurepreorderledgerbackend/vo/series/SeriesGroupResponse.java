package com.jong.figurepreorderledgerbackend.vo.series;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SeriesGroupResponse {

    private Long seriesGroupId;
    private String name;
    private List<SeriesResponse> children;
}
