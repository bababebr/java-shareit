package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class ItemDto {
    Long id;
    @NotNull(message = "name cannot be null.")
    @NotEmpty(message = "name cannot be empty.")
    String name;
    @NotNull(message = "description cannot be null.")
    @NotEmpty(message = "available cannot be empty.")
    String description;
    @NotNull(message = "available cannot be null.")
    Boolean available;

}
