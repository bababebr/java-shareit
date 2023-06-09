package ru.practicum.shareit.exception;

public class StateException extends RuntimeException{
    public StateException() {
        super();
    }

    public StateException(String message) {
        super(message);
    }
}
