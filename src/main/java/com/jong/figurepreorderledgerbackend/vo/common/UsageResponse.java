package com.jong.figurepreorderledgerbackend.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 삭제 전 경고용: 그 분류를 쓰고 있는 상품의 수 */
@Schema(description = "삭제 전 경고용: 그 분류를 쓰고 있는 상품 수")
@Getter
@RequiredArgsConstructor
public class UsageResponse {

    @Schema(description = "그 분류를 쓰고 있는 상품 수", example = "3")
    private final int count;
}
