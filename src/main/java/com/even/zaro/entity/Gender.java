package com.even.zaro.entity;

public enum Gender {
    MALE, FEMALE, OTHER, UNKNOWN;

    public static Gender fromKakao(String kakaoGender) {
        return switch (kakaoGender) {
            case "male" -> MALE;
            case "female" -> FEMALE;
            default -> UNKNOWN;
        };
    }
}