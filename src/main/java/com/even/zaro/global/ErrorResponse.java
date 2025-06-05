package com.even.zaro.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;

    public static ErrorResponse fail(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public static ErrorResponse fail(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message);
    }

    public static ErrorResponse fail(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
