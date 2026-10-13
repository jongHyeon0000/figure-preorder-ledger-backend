package com.jong.figurepreorderledgerbackend.vo.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 응답 {accessToken}. 이후 요청에 Authorization: Bearer 헤더로 보낸다
 */
@Getter
@RequiredArgsConstructor
public class LoginResponse {

    /** 서명된 JWT (유효 시간 24시간) */
    private final String accessToken;
}
