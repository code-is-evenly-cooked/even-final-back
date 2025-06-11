package com.even.zaro.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.even.zaro.global.validator.NicknameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNickname {

    String message() default "INVALID_NICKNAME_FORMAT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
