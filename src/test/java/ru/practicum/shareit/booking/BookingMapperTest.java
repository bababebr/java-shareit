package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class BookingMapperTest {
    private User user;
    private Booking booking;
    private BookingDto bookingDto;
    private LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
    private LocalDateTime end = LocalDateTime.of(2023, 1, 1, 13, 0);
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.create(1L, "name", "email");
        item = Item.create(1L, user, true, "desc", "name", 1L);
        booking = Booking.create(1L, item, user, start, end, BookingStatus.APPROVED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, start, end, BookingStatus.APPROVED);
    }
    @Test
    void bookingToBookingDto() {
        BookingDto dto = BookingMapper.bookingToBookingDto(booking);
        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getItem(), booking.getItem());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(dto.getStatus(), booking.getState());
    }

    @Test
    void bookingDtoToBooking() {
        Booking b = BookingMapper.bookingDtoToBooking(bookingDto, user, item);
        assertEquals(b.getId(), bookingDto.getId());
        assertEquals(b.getItem().getId(), item.getId());
        assertEquals(b.getBooker().getId(), user.getId());
        assertEquals(b.getStart(), bookingDto.getStart());
        assertEquals(b.getEnd(), bookingDto.getEnd());
        assertEquals(b.getBooker().getId(), user.getId());
    }

    @Test
    void bookingDtos() {
        List<BookingDto> list = BookingMapper.bookingDtos(List.of(booking));
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getId(), booking.getId());
    }

    @Test
    void bookingToBookingShort() {
        BookingDtoShort bookingDtoShort = BookingMapper.bookingToBookingShort(booking);
        assertEquals(bookingDtoShort.getId(), booking.getId());
        assertEquals(bookingDtoShort.getBookerId(), booking.getBooker().getId());
    }
}