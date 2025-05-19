package com.even.zaro.global.exception;

import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 커스텀 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustom(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        String message = "요청 url을 다시 확인해보세요 : " + ex.getMessage();
        log.error(message, ex);
        return ResponseEntity
                .status(ErrorCode.UNKNOWN_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.UNKNOWN_REQUEST, message));
    }

    // 이메일 중복 인증 발생 예외 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(ErrorCode.ILLEGAL_STATE.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.ILLEGAL_STATE));
    }

    // 파라미터로 받은 값의 인자가 맞지 않을 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("잘못된 요청: '%s' 값을 '%s' 타입으로 변환할 수 없습니다.", ex.getValue(), ex.getRequiredType().getSimpleName());
        log.error(message, ex);
        return ResponseEntity.status(ErrorCode.TYPE_MISMATCH
                .getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.TYPE_MISMATCH, message));
    }

    // 숫자 변환 실패
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<?>> handleNumberFormatException(NumberFormatException ex) {
        String message = "숫자 변환 오류: " + ex.getMessage();
        log.error(message, ex);
        return ResponseEntity.status(ErrorCode.NUMBER_FORMAT_ERROR.getHttpStatus()).body(ApiResponse.fail(ErrorCode.NUMBER_FORMAT_ERROR, message));
    }

    // 데이터베이스 접근 오류
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDataAccessException(DataAccessException ex) {
        String message = "데이터베이스 오류 발생: " + ex.getMessage();
        log.error(message, ex);
        return ResponseEntity
                .status(ErrorCode.DB_ACCESS_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.DB_ACCESS_ERROR, message));

    }

    // 요구되는 값이 비어있을 때
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        String message = "필수 데이터가 누락되었습니다 : " + ex.getMessage();
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(ErrorCode.NULL_POINTER.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.NULL_POINTER, message));

    }

    // 유효하지 않은 요청 파라미터
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = "잘못된 요청: " + ex.getMessage();
        log.error(message, ex);
        return ResponseEntity
                .status(ErrorCode.INVALID_ARGUMENT.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_ARGUMENT, message));
    }

    // 특정 예외를 명확히 처리
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResultException(NoResultException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(ErrorCode.NO_RESULT.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.NO_RESULT));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyResultException(EmptyResultDataAccessException ex) {
        String message = "결과가 존재하지 않습니다 : " + ex.getMessage();
        log.error(message, ex);
        return ResponseEntity
                .status(ErrorCode.EMPTY_RESULT.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.EMPTY_RESULT, message));
    }
}
