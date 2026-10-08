package com.jong.figurepreorderledgerbackend.vo.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 삭제 전 경고용: 그 분류를 쓰고 있는 상품의 수 */
@Getter
@RequiredArgsConstructor
public class UsageResponse {

    private final int count;
}
