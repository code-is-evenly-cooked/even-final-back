package com.even.zaro.global.exception.favorite;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class FavoriteException extends CustomException {
    private final ErrorCode errorCode;

    public FavoriteException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
