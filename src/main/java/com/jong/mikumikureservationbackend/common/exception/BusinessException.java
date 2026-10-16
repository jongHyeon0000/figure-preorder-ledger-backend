package com.jong.mikumikureservationbackend.common.exception;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final GlobalExceptionCode errorCode;

    public BusinessException(GlobalExceptionCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(GlobalExceptionCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
