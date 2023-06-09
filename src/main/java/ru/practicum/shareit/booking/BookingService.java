package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto add(Long bookerId, BookingDto bookingDto);

    BookingDto get(Long bookingId, Long userId);

    BookingDto approve(long bookerId, long bookingId, boolean approved);

    List<BookingDto> get(Long userId);

    List<BookingDto> getAllUserBookings(Long userId, String state, int from, int size);

    List<BookingDto> getAllOwnersBooking(Long userId, String state, int from, int size);
}
