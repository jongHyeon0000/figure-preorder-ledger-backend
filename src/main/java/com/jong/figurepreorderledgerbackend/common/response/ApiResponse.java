package com.jong.figurepreorderledgerbackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "모든 응답의 공통 래퍼 {code, message, data}")
public class ApiResponse<T> {

    private static final String OK = "OK";

    @Schema(description = "결과 코드. 성공은 OK, 실패는 에러 코드", example = "OK")
    private final String code;

    @Schema(description = "사용자에게 그대로 보여 줄 메시지. 성공, 실패 모두 항상 채워진다", example = "저장이 완료되었습니다.")
    private final String message;

    @Schema(description = "응답 본문. 실패하거나 돌려줄 값이 없으면 null", nullable = true)
    private final T data;

    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(OK, message, data);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(OK, message, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
