package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private User user;
    private Booking booking;
    private BookingDto bookingDto;
    private LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
    private LocalDateTime end = LocalDateTime.of(2023, 1, 1, 13, 0);
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = User.create(1L, "name", "email");
        item = Item.create(1L, user, true, "desc", "name", 1L);
        itemDto = ItemDto.create(1L, "name", "desc", true, 1L);
        booking = Booking.create(1L, item, user, start, end, BookingStatus.APPROVED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, start, end, BookingStatus.APPROVED);
    }

    @Test
    void itemToDto() {
        ItemDto dto = ItemMapper.itemToDto(item);
        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertEquals(dto.getRequestId(), item.getRequestId());
    }

    @Test
    void itemBookingHistoryDto() {
        ItemBookingHistoryDto b = ItemMapper.itemBookingHistoryDto(item);
        assertEquals(b.getId(), item.getId());
        assertEquals(b.getDescription(), item.getDescription());
        assertEquals(b.getName(), item.getName());
        assertEquals(b.getAvailable(), item.getAvailable());
        assertEquals(b.getRequestId(), item.getRequestId());
    }

    @Test
    void dtoToItem() {
        Item i = ItemMapper.dtoToItem(itemDto, user);
        assertEquals(i.getId(), itemDto.getId());
        assertEquals(i.getDescription(), itemDto.getDescription());
        assertEquals(i.getName(), itemDto.getName());
        assertEquals(i.getAvailable(), itemDto.getAvailable());
        assertEquals(i.getRequestId(), itemDto.getRequestId());
        assertEquals(i.getOwner().getId(), user.getId());
    }
}