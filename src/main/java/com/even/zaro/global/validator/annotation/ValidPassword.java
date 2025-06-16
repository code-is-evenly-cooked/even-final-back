package com.even.zaro.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.even.zaro.global.validator.PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "INVALID_PASSWORD_FORMAT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
