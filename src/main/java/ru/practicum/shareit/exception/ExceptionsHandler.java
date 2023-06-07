package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationException(final ValidationException e) {
        return new ErrorResponse("Validation Exception: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse duplicationException(final DuplicationException e) {
        return new ErrorResponse("Duplication Exception: ", e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchObjectException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemNotFoundException(final RuntimeException e) {
        return new ErrorResponse("Object Exception: ", e.getMessage());
    }
}