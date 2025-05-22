package com.even.zaro.global.exception.notification;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;

public class NotificationException extends CustomException {
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}