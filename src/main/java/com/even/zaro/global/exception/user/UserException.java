package com.even.zaro.global.exception.user;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class UserException extends CustomException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
