package com.even.zaro.entity;

public enum Gender {
    MALE, FEMALE, OTHER, UNKNOWN;

    public static Gender fromKakao(String kakaoGender) {
        if (kakaoGender == null) return UNKNOWN;

        return switch (kakaoGender) {
            case "male" -> MALE;
            case "female" -> FEMALE;
            default -> UNKNOWN;
        };
    }
}