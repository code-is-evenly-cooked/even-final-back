package com.even.zaro.global.validator;

import com.even.zaro.global.validator.annotation.ValidEmail;
import com.even.zaro.global.validator.annotation.ValidNickname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,12}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        return Pattern.matches(NICKNAME_REGEX, value);
    }
}
