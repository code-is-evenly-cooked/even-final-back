package com.even.zaro.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 예시 - ApiTestController
    EXAMPLE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "예시 예외 발생"),
    EXAMPLE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 오류가 발생했습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST,  "userId는 필수입니다."),
    UNAUTHORIZED_IMAGE_UPLOAD(HttpStatus.UNAUTHORIZED, "이미지 업로드 권한이 있는 사용자가 아닙니다."),
    INVALID_POST_ID(HttpStatus.BAD_REQUEST,  "postId는 필수입니다."),
    INVALID_UPLOAD_TYPE(HttpStatus.BAD_REQUEST, "type은 'profile' 또는 'post' 여야 합니다."),
    UNAUTHORIZED_IMAGE_DELETE(HttpStatus.UNAUTHORIZED, "이미지 삭제 권한이 았는 사용자가 아닙니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST,  "지원하지 않는 이미지 확장자입니다."),

    // 회원 User
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "이메일은 필수 입력 값입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "올바른 이메일 형식을 입력해주세요."),
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호는 필수 입력 값입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 6자 이상이어야 합니다."),

    NICKNAME_REQUIRED(HttpStatus.BAD_REQUEST, "닉네임은 필수 입력 값입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 2-12자, 영문, 한글, 숫자, -, _만 가능합니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 이메일이 아닙니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

        // 이메일 인증
    EMAIL_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 인증 요청입니다."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "인증 유효시간이 만료되었습니다."),
    EMAIL_TOKEN_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료된 요청입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),

        //jwt
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레쉬 토큰을 찾을 수 없습니다."),

    // 게시글 Post

    // 댓글 Comments
    COMMENT_NO_ASSOCIATED_POST(HttpStatus.INTERNAL_SERVER_ERROR, "댓글에 연결된 게시글이 존재하지 않습니다."),

    // 프로필 Profile
    FOLLOW_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    FOLLOW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 팔로우하고 있는 사용자입니다."),
    FOLLOW_UNFOLLOW_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 언팔로우할 수 없습니다."),
    FOLLOW_NOT_EXIST(HttpStatus.NOT_FOUND, "팔로우하지 않은 사용자는 언팔로우할 수 없습니다."),

    // 그룹 Group
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹을 찾지 못했습니다."),
    GROUP_ALREADY_DELETE(HttpStatus.NOT_FOUND, "이미 삭제한 그룹입니다."),
    GROUP_ALREADY_EXIST(HttpStatus.NOT_FOUND, "이미 존재하는 그룹 이름입니다."),
    UNAUTHORIZED_GROUP_DELETE(HttpStatus.UNAUTHORIZED, "다른 사용자의 그룹 삭제 시도입니다."),
    UNAUTHORIZED_GROUP_UPDATE(HttpStatus.UNAUTHORIZED, "다른 사용자의 그룹 수정 시도입니다."),

    // 즐겨찾기 favorite
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 즐겨찾기에 존재하는 장소는 추가할 수 없습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 즐겨찾기를 찾지 못했습니다."),
    UNAUTHORIZED_FAVORITE_UPDATE(HttpStatus.UNAUTHORIZED, "다른 사용자의 즐겨찾기 메모 수정 시도입니다."),
    UNAUTHORIZED_FAVORITE_DELETE(HttpStatus.UNAUTHORIZED, "다른 사용자의 즐겨찾기 삭제 시도입니다."),



    // 지도 Map
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾지 못했습니다."),


    // Health
    DB_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB 연결 실패"),

    // 기본 예외
    UNKNOWN_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류 요청 URL을 다시 확인해보십시오."),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 상태입니다."),
    NULL_POINTER(HttpStatus.BAD_REQUEST, "필수 데이터가 누락되었습니다."),
    INVALID_ARGUMENT( HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 일치하지 않습니다."),
    NUMBER_FORMAT_ERROR( HttpStatus.BAD_REQUEST, "숫자 형식 오류입니다."),
    DB_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 접근 오류입니다."),
    NO_RESULT(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    EMPTY_RESULT(HttpStatus.NOT_FOUND, "결과가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public String getCode() {
        return this.name();
    }
}