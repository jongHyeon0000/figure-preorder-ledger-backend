package com.jong.figurepreorderledgerbackend.vo.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String name;
}
