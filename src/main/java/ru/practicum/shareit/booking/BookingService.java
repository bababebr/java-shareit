package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto add(Long bookerId, BookingDto bookingDto);

    BookingDto update(Long bookerId, Long bookingId, BookingDto bookingDto, BookingStatus state);

    BookingDto get(Long bookingId, Long userId);

    BookingDto approve(long bookerId, long bookingId, boolean approved);

    List<BookingDto> getAllUsersBooking(Long userId);

    List<BookingDto> getAllOwnersBooking(Long userId, String state);
}
