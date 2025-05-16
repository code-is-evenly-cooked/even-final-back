package com.even.zaro.global.exception.user;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserException extends CustomException {

    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
