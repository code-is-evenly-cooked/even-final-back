package com.even.zaro.global.exception.notification;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.Getter;

@Getter
public class NotificationException extends CustomException {

    private final ErrorCode errorCode;

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}