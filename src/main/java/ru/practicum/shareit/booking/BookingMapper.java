package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {


    public static BookingDto bookingToBookingDto(Booking booking) {
        return BookingDto.create(booking.id,
                booking.getItemId(),
                booking.booker.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getState());
    }

    public static Booking bookingDtoToBooking(BookingDto bookingDto, User owner, User booker) {
        return Booking.create(bookingDto.getId(),
                bookingDto.getItemId(),
                owner,
                booker,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getState());
    }

    public static List<BookingDto> bookingDtos(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::bookingToBookingDto).collect(Collectors.toList());
    }


}
