package ru.practicum.shareit.validation.annotation;


import ru.practicum.shareit.validation.validator.BookingValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = BookingValidator.class)
@Documented
public @interface BookingValidation {

    String message() default "{BookingDto.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}