package com.even.zaro.global.exception.favoriteGroupEx;

public class FavoriteGroupException extends RuntimeException {
    public FavoriteGroupException(String message) {
        super(message);
    }

    public static FavoriteGroupException NotFoundGroupExcpetion() {
        return new FavoriteGroupException("해당 그룹을 찾지 못했습니다.");
    }

    public static FavoriteGroupException AlreadyDeletedGroupExcpetion() {
        return new FavoriteGroupException("이미 삭제한 그룹입니다.");
    }
}
