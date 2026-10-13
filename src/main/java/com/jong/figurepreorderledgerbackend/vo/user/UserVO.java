package com.jong.figurepreorderledgerbackend.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 계정 (테이블 users)
 * 가입 요청(loginId, email, name, password만 읽음), 내 정보 응답, 매퍼 조회 결과에 같이 쓴다. [request] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO {

    /** PK */
    private Long userId;

    /** 로그인 아이디 (영문, 숫자 4~30자, 대소문자 구분) */
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{4,30}$", message = "영문과 숫자 4~30자여야 합니다")
    private String loginId;

    /** 이메일 (소문자로 저장) */
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    /** 닉네임 (한글 포함 20자 이하) */
    @NotBlank
    @Size(max = 20)
    private String name;

    /** 비밀번호 해시(BCrypt). 응답과 요청 JSON에는 절대 나가거나 들어오지 않는다 */
    @JsonIgnore
    private String passwordHash;

    /** 가입일 */
    private LocalDateTime createdAt;

    /** 수정일 */
    private LocalDateTime updatedAt;

    /** [request] 가입 비밀번호 (평문, 8~20자). 요청에서만 읽고 응답에는 나가지 않는다 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
