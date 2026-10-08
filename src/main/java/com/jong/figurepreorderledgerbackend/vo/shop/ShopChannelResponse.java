package com.jong.figurepreorderledgerbackend.vo.shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShopChannelResponse {

    private Long id;
    private String name;
    private List<ShopResponse> children;
}
