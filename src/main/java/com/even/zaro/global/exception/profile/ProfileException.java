package com.even.zaro.global.exception.profile;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class ProfileException extends CustomException {
    public ProfileException(ErrorCode errorCode) {
        super(errorCode);
    }
}
