package com.even.zaro.global.exception.place;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class PlaceException extends CustomException {
    public PlaceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
