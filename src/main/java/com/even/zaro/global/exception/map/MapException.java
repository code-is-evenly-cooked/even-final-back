package com.even.zaro.global.exception.map;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class MapException extends CustomException {
    private final ErrorCode errorCode;
    public MapException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
