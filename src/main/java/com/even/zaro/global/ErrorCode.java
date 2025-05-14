package com.even.zaro.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 기본 예외
    UNKNOWN_REQUEST("UNKNOWN_REQUEST", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류 요청 URL을 다시 확인해보십시오."),
    ILLEGAL_STATE("ILLEGAL_STATE", HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 상태입니다."),
    NULL_POINTER("NULL_POINTER", HttpStatus.BAD_REQUEST, "필수 데이터가 누락되었습니다."),
    INVALID_ARGUMENT("INVALID_ARGUMENT", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TYPE_MISMATCH("TYPE_MISMATCH", HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 일치하지 않습니다."),
    NUMBER_FORMAT_ERROR("NUMBER_FORMAT_ERROR", HttpStatus.BAD_REQUEST, "숫자 형식 오류입니다."),
    DB_ACCESS_ERROR("DB_ACCESS_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 접근 오류입니다."),
    NO_RESULT("NO_RESULT", HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    EMPTY_RESULT("EMPTY_RESULT", HttpStatus.NOT_FOUND, "결과가 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 오류가 발생했습니다."),

    // 커스텀 예외
    EXAMPLE_EXCEPTION("EXAMPLE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR, "예시 예외 발생"),
    USER_EXCEPTION("USER_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR, "사용자 예외 발생");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}