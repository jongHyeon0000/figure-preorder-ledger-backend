package com.jong.figurepreorderledgerbackend.vo.series.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeriesResponse {

    private Long seriesId;
    private Long seriesGroupId;
    private String name;
}
