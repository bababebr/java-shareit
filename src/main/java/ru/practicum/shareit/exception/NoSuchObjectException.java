package ru.practicum.shareit.exception;

public class NoSuchObjectException extends RuntimeException {

    public NoSuchObjectException(String message) {
        super(message);
    }
}