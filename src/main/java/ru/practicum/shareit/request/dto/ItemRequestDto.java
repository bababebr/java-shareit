package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.optional.qual.Present;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class ItemRequestDto {
    Long id;
    @NotNull(message = "Description cannot be null.")
    String description;
    @NotNull(message = "Creating date cannot be null.")
    @Present
    LocalDateTime created;
    List<Item> items = new ArrayList<>();
}