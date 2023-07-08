package java.ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    private User user;
    private Booking booking;
    private BookingDto bookingDto;
    private LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
    private LocalDateTime end = LocalDateTime.of(2023, 1, 1, 13, 0);
    private Item item;
    private ItemDto itemDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.create(1L, "name", "email");
        item = Item.create(1L, user, true, "desc", "name", 1L);
        itemDto = ItemDto.create(1L, "name", "desc", true, 1L);
        booking = Booking.create(1L, item, user, start, end, BookingStatus.APPROVED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, start, end, BookingStatus.APPROVED);
        itemRequestDto = ItemRequestDto.create(1L, "request 1", start, new ArrayList<>());
        itemRequest = ItemRequest.create(1L, "request 1", start, user, item);
    }

    @Test
    void requestToDto() {
        ItemRequestDto i = ItemRequestMapper.requestToDto(itemRequest);
        assertEquals(i.getId(), itemRequest.getId());
        assertEquals(i.getCreated(), itemRequest.getCreated());
        assertEquals(i.getDescription(), itemRequest.getDescription());
    }

    @Test
    void dtoToRequest() {
        ItemRequest ir = ItemRequestMapper.dtoToRequest(itemRequestDto, user);
        assertEquals(ir.getId(), itemRequestDto.getId());
        assertEquals(ir.getRequester().getId(), user.getId());
        assertEquals(ir.getCreated(), itemRequestDto.getCreated());
        assertEquals(ir.getDescription(), itemRequestDto.getDescription());
    }
}