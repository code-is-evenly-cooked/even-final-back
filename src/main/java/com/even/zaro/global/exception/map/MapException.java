package com.even.zaro.global.exception.map;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class MapException extends CustomException {
    public MapException(ErrorCode errorCode) {
        super(errorCode);
    }
}
