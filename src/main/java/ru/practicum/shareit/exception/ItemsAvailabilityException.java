package ru.practicum.shareit.exception;

public class ItemsAvailabilityException extends RuntimeException{
    public ItemsAvailabilityException() {
        super();
    }

    public ItemsAvailabilityException(String message) {
        super(message);
    }
}
