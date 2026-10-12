package com.jong.figurepreorderledgerbackend.vo.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureTypeGroupResponse {

    private Long figureTypeGroupId;
    private String name;
    private List<FigureTypeResponse> children;
}
