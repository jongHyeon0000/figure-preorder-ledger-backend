package com.jong.figurepreorderledgerbackend.vo.figure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FigureTypeInfo {

    private Long figureTypeId;
    private String name;
    private Long figureTypeGroupId;
    private String figureTypeGroupName;
}
