package com.even.zaro.global.validator.annotation;

import com.even.zaro.global.validator.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "INVALID_ENUM";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
