package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
import ru.practicum.shareit.exception.NoSuchObjectException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    ObjectMapper mapper = new ObjectMapper();
    @MockBean
    BookingClient bookingService;
    Long owner;
    Long booker;
    Long item;
    LocalDateTime start;
    LocalDateTime end;
    BookingDto bookingDto;
    @Autowired
    private MockMvc mvc;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        owner = 1L;
        booker = 2L;
        item = 1L;
        start = LocalDateTime.now().plusMinutes(10);
        end = start.plusHours(1);
        bookingDto = BookingDto.create(1L, item, item, booker, start,
                end, BookingStatus.WAITING);
    }

    @Test
    void add() throws Exception {
        when(bookingService.add(anyLong(), any(BookingDto.class)))
                .thenReturn(new ResponseEntity<>(bookingDto, HttpStatus.OK));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addWrongDate() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusHours(1));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner)
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
                        .header("X-Sharer-User-Id", owner)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addBookingItemNotAvailable() throws Exception {
        when(bookingService.add(anyLong(), any(BookingDto.class)))
                .thenAnswer(invocationOnMock -> {
                    if (false) {
                        return status();
                    }
                    throw new ItemsAvailabilityException("");
                });
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner)
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
                            .equals(owner)) {
                        throw new NoSuchObjectException("");
                    }
                    return status();
                });
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner)
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

                    if (!booker.equals(bookerId)) {
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
        mvc.perform(patch("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

        //Booking Not Found
        mvc.perform(patch("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", booker)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
        //User Not Found
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", owner)
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
                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchElementException();
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getUserNotFound() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    Long userId = invocationOnMock.getArgument(1, Long.class);
                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchElementException();
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", owner)
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
                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("");
                    }
                    if (bookingId.equals(bookingDto.getId())) {
                        return bookingDto;
                    }
                    throw new NoSuchObjectException("Booking not found");
                });
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", booker)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
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

                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
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

                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.WAITING);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
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
                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
                    }

                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.APPROVED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
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

                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.REJECTED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
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

                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.CANCELLED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "CANCELLED")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
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

                    if (!userId.equals(booker)) {
                        throw new NoSuchObjectException("User not found");
                    }
                    if (state.equals(bookingDto.getStatus().toString()) || state.equals("ALL")) {
                        return List.of(bookingDto);
                    }
                    throw new NoSuchObjectException("Bookings with State not found");
                });
        bookingDto.setStatus(BookingStatus.CANCELLED);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner", bookingDto.getId())
                        .header("X-Sharer-User-Id", booker)
                        .param("state", "CANCELLED")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}