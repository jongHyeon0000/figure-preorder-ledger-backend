package com.jong.figurepreorderledgerbackend.vo.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FigureTypeResponse {

    private Long id;
    private String name;
    private List<FigureSubtypeResponse> children;
}
