package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler({ValidationException.class, ItemsAvailabilityException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationException(final RuntimeException e) {
        return new ErrorResponse("BadRequest Exception: ", e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchObjectException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemNotFoundException(final RuntimeException e) {
        return new ErrorResponse("Object Exception: ", e.getMessage());
    }
}