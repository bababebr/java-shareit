package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    ObjectMapper mapper = new ObjectMapper();
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private User owner;
    private User booker;
    private Item item;
    private BookingDto bookingDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        owner = User.create(1L, "owner", "owner@mail.ru");
        booker = User.create(2L, "booker", "booker@mail.ru");
        item = Item.create(1L, owner, true, "desc", "item 1", null);
        start = LocalDateTime.now().plusMinutes(10);
        end = start.plusHours(1);
        bookingDto = BookingDto.create(1L, item.getId(), item, booker, start,
                end, BookingStatus.WAITING);
    }

    @Test
    void add() throws Exception {
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
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void addWrongDate() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusHours(1));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addStatusIsNull() throws Exception {
        bookingDto.setStatus(null);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addBookingItemNotAvailable() throws Exception {
        item.setAvailable(false);
        when(bookingService.add(anyLong(), any(BookingDto.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(1, BookingDto.class).getItem().getAvailable()) {
                        return status();
                    }
                    throw new ItemsAvailabilityException("");
                });
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addBookingByOwnerHimself() throws Exception {
        when(bookingService.add(anyLong(), any(BookingDto.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(0, Long.class)
                            .equals(invocationOnMock.getArgument(1, BookingDto.class).getItem().getOwner().getId())) {
                        throw new NoSuchObjectException("");
                    }
                    return status();
                });
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    Long bookerId = invocationOnMock.getArgument(0, Long.class);
                    Long bookingId = invocationOnMock.getArgument(1, Long.class);
                    Boolean isApproved = invocationOnMock.getArgument(2, Boolean.class);

                    if (!booker.getId().equals(bookerId)) {
                        throw new NoSuchObjectException("");
                    }
                    if (!bookingDto.getId().equals(bookingId)) {
                        throw new NoSuchObjectException("");
                    }
                    if (!bookingDto.getStatus().equals(BookingStatus.WAITING)) {
                        throw new ItemsAvailabilityException("");
                    }
                    if (isApproved) {
                        bookingDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingDto.setStatus(BookingStatus.REJECTED);
                    }
                    return bookingDto;
                });
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        //Booking Not Found
        mvc.perform(patch("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
        //User Not Found
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

        //Status not Waiting
        bookingDto.setStatus(BookingStatus.APPROVED);
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", bookingDto.getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void get() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    Long userId = invocationOnMock.getArgument(1, Long.class);
                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchElementException();
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getUserNotFound() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    Long userId = invocationOnMock.getArgument(1, Long.class);
                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchElementException();
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getUserBookingNotFound() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    Long userId = invocationOnMock.getArgument(1, Long.class);
                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchObjectException("Booking not found");
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(MockMvcRequestBuilders.get("/bookings", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("from", "100")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].status", is("WAITING")));
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }

                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class));
    }


    @Test
    void getOwnerBookingsWaiting() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }

                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.WAITING);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].status", is("WAITING")));
    }

    @Test
    void getOwnerBookingsApproved() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }
                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }

                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.APPROVED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }

    @Test
    void getOwnerBookingsRejected() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }

                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.REJECTED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].status", is("REJECTED")));
    }

    @Test
    void getOwnerBookingsCancelled() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }

                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.CANCELLED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "CANCELLED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].status", is("CANCELLED")));
    }

    @Test
    void getOwnerBookingsWrongPaging() throws Exception {
        when(bookingService.getAllOwnersBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    String state = invocationOnMock.getArgument(1, String.class);
                    int from = invocationOnMock.getArgument(2, Integer.class);
                    int size = invocationOnMock.getArgument(3, Integer.class);

                    if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
                        throw new ItemsAvailabilityException("Invalid paging size");
                    }
                    if (from == -2) {
                        return new ArrayList<>();
                    }

                    if (!userId.equals(booker.getId())) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.CANCELLED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "CANCELLED")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}