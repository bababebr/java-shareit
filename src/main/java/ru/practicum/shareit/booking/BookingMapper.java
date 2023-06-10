package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto bookingToBookingDto(Booking booking) {
        return BookingDto.create(booking.id,
                booking.item.getId(),
                booking.item,
                booking.booker,
                booking.getStart(),
                booking.getEnd(),
                booking.getState());
    }

    public static Booking bookingDtoToBooking(BookingDto bookingDto, User owner, User booker) {
        return Booking.create(bookingDto.getId(),
                bookingDto.getItem(),
                owner,
                booker,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getStatus());
    }

    public static List<BookingDto> bookingDtos(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::bookingToBookingDto).collect(Collectors.toList());
    }

    public static BookingDtoShort bookingToBookingShort(Booking booking) {
        return BookingDtoShort.create(booking.getId(),
                booking.getBooker().getId());
    }

}
