package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    private Booking booking;
    private BookingDto bookingDto;
    private Item item;
    private User user;
    private User owner;
    private LocalDateTime startFuture;
    private LocalDateTime startPast;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = User.create(1L, "user", "user@mail.ru");
        owner = User.create(2L, "owner", "owner@mail.ru");
        item = Item.create(1L, owner, true, "Item 1", "Item", null);
        startFuture = LocalDateTime.now().plusHours(1);
        startPast = LocalDateTime.now().minusHours(1);
        end = LocalDateTime.now().plusHours(2);
        booking = Booking.create(1L, item, user, startFuture,
                end, BookingStatus.WAITING);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, startFuture, end, BookingStatus.WAITING);
    }

    @Test
    void addBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        BookingDto returnBooking = bookingService.add(user.getId(), bookingDto);
        Assertions.assertEquals(booking.getItem(), returnBooking.getItem());
        Assertions.assertEquals(booking.getId(), returnBooking.getId());
        Assertions.assertEquals(booking.getStart(), returnBooking.getStart());
        Assertions.assertEquals(booking.getState(), returnBooking.getStatus());
        Assertions.assertEquals(booking.getEnd(), returnBooking.getEnd());
    }

    @Test
    void addBookingItemNotFound() {
        when(itemRepository.findById(item.getId()))
                .thenThrow(new NoSuchObjectException("Item with ID=1 not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "Item with ID=1 not found");
    }

    @Test
    void addBookingUserNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(user.getId()))
                .thenThrow(new NoSuchObjectException("User with ID=1 not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "User with ID=1 not found");
    }

    @Test
    void addBookingByOwner() {
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "Booking cannot be done by owner.");
    }

    @Test
    void addBookingNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "Item with ID=1 is not available.");
    }

    @Test
    void addBookingPeriodIsOk() {
        LocalDateTime existStart = startPast.minusMinutes(10);
        LocalDateTime existEnd = startPast.minusMinutes(5);
        Booking existBooking = Booking.create(2L, item, user, existStart, existEnd, BookingStatus.APPROVED);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(existBooking));
        BookingDto returnBooking = bookingService.add(user.getId(), bookingDto);
        Assertions.assertEquals(booking.getItem(), returnBooking.getItem());
        Assertions.assertEquals(booking.getId(), returnBooking.getId());
        Assertions.assertEquals(booking.getStart(), returnBooking.getStart());
        Assertions.assertEquals(booking.getState(), returnBooking.getStatus());
        Assertions.assertEquals(booking.getEnd(), returnBooking.getEnd());
    }

    @Test
    void addBookingPeriodIsOccupied() {
        LocalDateTime existStart = startPast.minusMinutes(10);
        LocalDateTime existEnd = end.minusMinutes(5);
        Booking existBooking = Booking.create(2L, item, user, existStart, existEnd, BookingStatus.APPROVED);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(existBooking));

        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "Item with ID=1 is booked for this period.");
    }

    @Test
    void addBookingPeriodIsOccupiedCase2() {
        LocalDateTime existStart = startFuture.plusMinutes(10);
        LocalDateTime existEnd = end.plusHours(2);
        Booking existBooking = Booking.create(2L, item, user, existStart, existEnd, BookingStatus.APPROVED);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(existBooking));

        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.add(user.getId(), bookingDto));
        assertEquals(e.getMessage(), "Item with ID=1 is booked for this period.");
    }

    @Test
    void addBookingPeriodIsOkOneByOne() {
        LocalDateTime existStart = end;
        LocalDateTime existEnd = end.plusHours(2);
        Booking existBooking = Booking.create(2L, item, user, existStart, existEnd, BookingStatus.APPROVED);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(existBooking));
        BookingDto returnBooking = bookingService.add(user.getId(), bookingDto);
        Assertions.assertEquals(booking.getItem(), returnBooking.getItem());
        Assertions.assertEquals(booking.getId(), returnBooking.getId());
        Assertions.assertEquals(booking.getStart(), returnBooking.getStart());
        Assertions.assertEquals(booking.getState(), returnBooking.getStatus());
        Assertions.assertEquals(booking.getEnd(), returnBooking.getEnd());
    }

    @Test
    void get() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
    }

    @Test
    void getBookingWithWrongId() {
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> bookingService.get(booking.getId(), user.getId()));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingWithWrongUser() {
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> bookingService.getAllUserBookings(user.getId(), "APPROVED", 0, 0));
        assertEquals("User with ID=1 not found", exception.getMessage());
    }

}
