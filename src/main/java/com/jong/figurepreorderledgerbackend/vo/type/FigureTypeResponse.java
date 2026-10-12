package com.jong.figurepreorderledgerbackend.vo.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FigureTypeResponse {

    private Long figureTypeId;
    private Long figureTypeGroupId;
    private String name;
}
