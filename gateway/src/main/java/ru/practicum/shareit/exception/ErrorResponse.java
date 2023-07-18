package ru.practicum.shareit.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ErrorResponse {
    String error;
    String description;
}