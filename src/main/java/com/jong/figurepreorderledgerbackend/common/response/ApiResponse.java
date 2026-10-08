package com.jong.figurepreorderledgerbackend.common.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private static final String OK = "OK";

    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(OK, null, data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(OK, null, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
