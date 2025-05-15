package com.even.zaro.global.exception.post;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class PostException extends CustomException {
    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
