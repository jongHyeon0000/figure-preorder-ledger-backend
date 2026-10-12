package com.jong.figurepreorderledgerbackend.vo.figure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FigureShopInfo {

    private Long shopId;
    private String name;
    private Long shopGroupId;
    private String shopGroupName;
}
