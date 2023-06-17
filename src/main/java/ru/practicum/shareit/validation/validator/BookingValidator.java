package ru.practicum.shareit.validation.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.annotation.BookingValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingValidator implements ConstraintValidator<BookingValidation, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime start = bookingDto.getStart();
        if (end.isBefore(start) || start.isEqual(end)) {
            return false;
        }
        return true;
    }
}
