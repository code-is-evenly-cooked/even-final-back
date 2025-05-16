package com.even.zaro.global.exception.favorite;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class FavoriteException extends CustomException {
    public FavoriteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
