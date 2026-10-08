package com.jong.figurepreorderledgerbackend.vo.figure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FigureSubtypeInfo {

    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
}
