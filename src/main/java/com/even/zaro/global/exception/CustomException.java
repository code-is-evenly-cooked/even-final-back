package com.even.zaro.global.exception;

import com.even.zaro.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final String message;

    public CustomException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public CustomException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.status = errorCode.getHttpStatus();
        this.message = errorCode.getDefaultMessage();
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.status = errorCode.getHttpStatus();
        this.message = customMessage;
    }

}
