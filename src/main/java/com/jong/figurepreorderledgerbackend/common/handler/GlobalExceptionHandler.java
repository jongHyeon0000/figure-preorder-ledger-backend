package com.jong.figurepreorderledgerbackend.common.handler;

import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        return build(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBodyValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(GlobalExceptionCode.VALIDATION_FAILED, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return build(GlobalExceptionCode.VALIDATION_FAILED, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        return build(GlobalExceptionCode.BAD_REQUEST, "요청 본문을 읽을 수 없습니다");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return build(GlobalExceptionCode.BAD_REQUEST, "'" + e.getName() + "' 값이 올바르지 않습니다");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        return build(GlobalExceptionCode.BAD_REQUEST, "'" + e.getParameterName() + "' 값이 필요합니다");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPart(MissingServletRequestPartException e) {
        return build(GlobalExceptionCode.BAD_REQUEST, "'" + e.getRequestPartName() + "' 파일이 필요합니다");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return build(GlobalExceptionCode.BAD_REQUEST, "업로드 가능한 크기를 초과했습니다");
    }

    /*
     * 위에서 잡지 못한 예외의 마지막 처리.
     * 스프링이 던지는 404, 405, 415 등(ErrorResponse)은 상태에 맞는 코드로, 나머지는 500으로 응답한다.
     * 인증을 붙일 때는 AccessDeniedException, AuthenticationException이 여기서 500으로 바뀌지 않도록 따로 처리해야 한다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        if (e instanceof ErrorResponse) {
            GlobalExceptionCode errorCode = fromStatus(((ErrorResponse) e).getStatusCode().value());
            return build(errorCode, errorCode.getDefaultMessage());
        }
        log.error("처리되지 않은 예외", e);
        return build(GlobalExceptionCode.INTERNAL_ERROR, GlobalExceptionCode.INTERNAL_ERROR.getDefaultMessage());
    }

    private GlobalExceptionCode fromStatus(int status) {
        if (status == HttpStatus.NOT_FOUND.value()) {
            return GlobalExceptionCode.NOT_FOUND;
        }
        if (status == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            return GlobalExceptionCode.METHOD_NOT_ALLOWED;
        }
        if (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
            return GlobalExceptionCode.UNSUPPORTED_MEDIA_TYPE;
        }
        if (status >= 400 && status < 500) {
            return GlobalExceptionCode.BAD_REQUEST;
        }
        return GlobalExceptionCode.INTERNAL_ERROR;
    }

    private ResponseEntity<ApiResponse<Void>> build(GlobalExceptionCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.name(), message));
    }
}
