package com.jong.mikumikureservationbackend.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 응답 {accessToken}. 이후 요청에 Authorization: Bearer 헤더로 보낸다
 */
@Schema(description = "로그인 응답. 이후 요청에 Authorization: Bearer 헤더로 보낸다")
@Getter
@RequiredArgsConstructor
public class LoginResponse {

    /** 서명된 JWT (유효 시간 24시간) */
    @Schema(description = "서명된 JWT (유효 시간 24시간)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private final String accessToken;
}
