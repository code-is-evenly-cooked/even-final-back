package com.even.zaro.global.exception.group;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class GroupException extends CustomException {
    private final ErrorCode errorCode;

    public GroupException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }


}
