package com.jong.figurepreorderledgerbackend.vo.shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopResponse {

    private Long shopId;
    private Long shopGroupId;
    private String name;
}
