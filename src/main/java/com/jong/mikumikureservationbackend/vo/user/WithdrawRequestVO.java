package com.jong.mikumikureservationbackend.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 탈퇴 요청 {password}. 인증된 상태에서 본인 확인을 한 번 더 한다
 */
@Schema(description = "탈퇴 요청. 인증된 상태에서 본인 확인을 한 번 더 한다")
@Getter
@Setter
@NoArgsConstructor
public class WithdrawRequestVO {

    /** 현재 비밀번호 (평문) */
    @Schema(description = "현재 비밀번호 (평문)", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}
