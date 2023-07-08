package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class ItemDto {
    Long id;
    @NotBlank(message = "name cannot be null or empty.")
    String name;
    @NotBlank(message = "description cannot be null or empty.")
    String description;
    @NotNull(message = "available cannot be null.")
    Boolean available;
    Long requestId;
}