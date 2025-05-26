package com.even.zaro.global.exception.profile;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class ProfileException extends CustomException {

    private final ErrorCode errorCode;

    public ProfileException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
