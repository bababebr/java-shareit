package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto bookingToBookingDto(Booking booking) {
        return BookingDto.create(booking.getId(),
                booking.getItem().getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getState());
    }

    public static Booking bookingDtoToBooking(BookingDto bookingDto, User booker, Item item) {
        return Booking.create(bookingDto.getId(),
                item,
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
