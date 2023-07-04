package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.CommentDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class ItemBookingHistoryDto {
    @NotNull
    Long id;
    @NotBlank(message = "name cannot be null or empty.")
    String name;
    @NotBlank(message = "description cannot be null or empty.")
    String description;
    @NotNull(message = "available cannot be null.")
    Boolean available;
    BookingDtoShort lastBooking = null;
    BookingDtoShort nextBooking = null;
    List<CommentDTO> comments;
    Long requestId = null;
}
