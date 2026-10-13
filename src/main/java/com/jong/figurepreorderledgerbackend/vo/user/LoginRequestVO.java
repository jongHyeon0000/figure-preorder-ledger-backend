package com.jong.figurepreorderledgerbackend.vo.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 {loginId, password}. 가입 규칙은 검사하지 않아 형식이 틀려도 같은 401로 응답한다(규칙이 드러나지 않게)
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestVO {

    /** 로그인 아이디 */
    @NotBlank
    private String loginId;

    /** 비밀번호 (평문) */
    @NotBlank
    private String password;
}
