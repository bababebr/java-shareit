package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.annotation.BookingValidation;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
@BookingValidation
public class BookingDto {
    Long id;
    Long itemId;
    Item item;
    User booker;
    @NotNull
    @Future
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    @NotNull
    BookingStatus status = BookingStatus.WAITING;
}
