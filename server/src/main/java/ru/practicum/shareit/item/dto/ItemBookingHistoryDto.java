package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.CommentDTO;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class ItemBookingHistoryDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoShort lastBooking = null;
    BookingDtoShort nextBooking = null;
    List<CommentDTO> comments;
    Long requestId = null;
}
