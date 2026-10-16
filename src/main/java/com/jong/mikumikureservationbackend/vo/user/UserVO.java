package com.jong.mikumikureservationbackend.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "계정. 가입 요청(loginId, email, name, password만 읽음)과 내 정보 응답에 같이 쓴다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO {

    /** PK */
    @Schema(description = "계정 id", example = "1")
    private Long userId;

    /** 로그인 아이디 (영문, 숫자 4~30자, 대소문자 구분) */
    @Schema(description = "로그인 아이디 (영문, 숫자 4~30자, 대소문자 구분)", example = "jong1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{4,30}$", message = "영문과 숫자 4~30자여야 합니다")
    private String loginId;

    /** 이메일 (소문자로 저장) */
    @Schema(description = "이메일 (소문자로 저장)", example = "jong@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    /** 닉네임 (한글 포함 20자 이하) */
    @Schema(description = "닉네임 (한글 포함 20자 이하)", example = "종현", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 20)
    private String name;

    /** 비밀번호 해시(BCrypt). 응답과 요청 JSON에는 절대 나가거나 들어오지 않는다 */
    @JsonIgnore
    private String passwordHash;

    /** 가입일 */
    @Schema(description = "가입일시", example = "2026-10-12T21:30:00")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시", example = "2026-10-12T21:30:00")
    private LocalDateTime updatedAt;

    /** [thumbnail_image joined] 프로필 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 생략) */
    @Schema(description = "프로필 썸네일 이미지 주소 (/uploads/ + file_path, 없으면 생략) (내 정보 조회 응답에서만 나온다)")
    private String thumbnailUrl;

    /** [request] 가입 비밀번호 (평문, 8~20자). 요청에서만 읽고 응답에는 나가지 않는다 */
    @Schema(description = "가입 비밀번호 (평문, 8~20자). 요청에서만 읽고 응답에는 나가지 않는다", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
