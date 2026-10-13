package com.jong.figurepreorderledgerbackend.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 {loginId, password}. 가입 규칙은 검사하지 않아 형식이 틀려도 같은 401로 응답한다(규칙이 드러나지 않게)
 */
@Schema(description = "로그인 요청. 가입 규칙은 검사하지 않아 형식이 틀려도 같은 401로 응답한다")
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestVO {

    /** 로그인 아이디 */
    @Schema(description = "로그인 아이디", example = "jong1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String loginId;

    /** 비밀번호 (평문) */
    @Schema(description = "비밀번호 (평문)", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}
