package com.jong.figurepreorderledgerbackend.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 데이터 형식입니다"),
    PROTECTED_CATEGORY(HttpStatus.BAD_REQUEST, "기타는 수정하거나 삭제할 수 없습니다"),
    LAST_CHILD_CATEGORY(HttpStatus.BAD_REQUEST, "마지막 하위 항목은 삭제할 수 없습니다"),
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "이미 같은 이름이 있습니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다");

    private final HttpStatus status;
    private final String defaultMessage;
}
