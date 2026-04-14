package com.epam.rd.autocode.spring.project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PastOrPresentYearValidator.class)
public @interface PastOrPresentYear {
    String message() default "{error.validation.maxyear}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}