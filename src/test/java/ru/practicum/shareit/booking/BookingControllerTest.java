package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController controller;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
        mapper.findAndRegisterModules();
    }

    @Test
    void add() throws Exception{
        User owner = User.create(1L, "owner", "owner@mail.ru");
        User booker = User.create(2L, "booker", "booker@mail.ru");
        Item item = Item.create(1L, owner, true, "desc", "item 1", null);
        BookingDto bookingDto = BookingDto.create(1L, item.getId(), item, booker, LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusHours(1), BookingStatus.WAITING);

        when(bookingService.add(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void addWrongDate() throws Exception{
        User owner = User.create(1L, "owner", "owner@mail.ru");
        User booker = User.create(2L, "booker", "booker@mail.ru");
        Item item = Item.create(1L, owner, true, "desc", "item 1", null);
        BookingDto bookingDto = BookingDto.create(1L, item.getId(), item, booker, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), BookingStatus.WAITING);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addStatusIsNull() throws Exception{
        User owner = User.create(1L, "owner", "owner@mail.ru");
        User booker = User.create(2L, "booker", "booker@mail.ru");
        Item item = Item.create(1L, owner, true, "desc", "item 1", null);
        BookingDto bookingDto = BookingDto.create(1L, item.getId(), item, booker, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void approve() {
    }

    @Test
    void get() {
    }

    @Test
    void getUserBookings() {
    }

    @Test
    void getOwnerBookings() {
    }
}