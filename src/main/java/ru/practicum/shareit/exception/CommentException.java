package ru.practicum.shareit.exception;

public class CommentException extends RuntimeException {
    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }
}
