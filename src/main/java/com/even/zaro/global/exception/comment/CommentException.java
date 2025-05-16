package com.even.zaro.global.exception.comment;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class CommentException extends CustomException {
    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}