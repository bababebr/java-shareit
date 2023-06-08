package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingStatus;

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
public class BookingDto {
    @NotNull
    Long id;
    @NotNull
    Long itemId;
    @NotNull
    Long bookerId;
    LocalDateTime start;
    @Future
    LocalDateTime end;
    @NotNull
    BookingStatus state;
}
