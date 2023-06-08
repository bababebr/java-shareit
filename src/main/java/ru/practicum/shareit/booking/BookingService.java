package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto add(Long bookerId, BookingDto bookingDto);

    BookingDto update(Long bookerId, Long bookingId, BookingDto bookingDto, BookingStatus state);

    List<BookingDto> get(Long bookingId, Long bookerId);

    BookingDto approve(long bookerId, long bookingId, boolean approved);
}
