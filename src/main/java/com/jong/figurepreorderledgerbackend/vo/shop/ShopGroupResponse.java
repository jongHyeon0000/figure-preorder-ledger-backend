package com.jong.figurepreorderledgerbackend.vo.shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShopGroupResponse {

    private Long shopGroupId;
    private String name;
    private List<ShopResponse> children;
}
