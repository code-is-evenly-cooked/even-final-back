package com.even.zaro.global.exception.group;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class GroupException extends CustomException {
    public GroupException(ErrorCode errorCode) {
        super(errorCode);
    }
}
