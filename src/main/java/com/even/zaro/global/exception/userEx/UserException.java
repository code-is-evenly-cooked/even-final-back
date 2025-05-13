package com.even.zaro.global.exception.userEx;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }


    public static UserException NotFoundUserException() {
        return new UserException("해당 유저를 찾을 수 없습니다.");
    }
}
