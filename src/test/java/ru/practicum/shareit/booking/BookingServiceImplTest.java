package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
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
    private Booking bookingCangelled;
    private Booking bookingApproved;
    private Booking bookingRejected;
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
        booking = Booking.create(1L, item, user, startFuture, end, BookingStatus.WAITING);
        bookingApproved = Booking.create(2L, item, user, startPast, end, BookingStatus.APPROVED);
        bookingCangelled = Booking.create(3L, item, user, startPast, end, BookingStatus.CANCELLED);
        bookingRejected = Booking.create(4L, item, user, startPast, startPast.plusMinutes(3), BookingStatus.REJECTED);
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
    void getBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("Booking not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.get(booking.getId(), user.getId()));
        assertEquals(e.getMessage(), "Booking not found");
    }

    @Test
    void getBookingNotBookerOrOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.get(booking.getId(), 3L));
        assertEquals(e.getMessage(), "Access denied.");
    }

    @Test
    void getBookingByBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        BookingDto dto = bookingService.get(booking.getId(), user.getId());
        Assertions.assertEquals(booking.getItem(), dto.getItem());
        Assertions.assertEquals(booking.getId(), dto.getId());
        Assertions.assertEquals(booking.getStart(), dto.getStart());
        Assertions.assertEquals(booking.getState(), dto.getStatus());
        Assertions.assertEquals(booking.getEnd(), dto.getEnd());
    }

    @Test
    void getBookingByOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        BookingDto dto = bookingService.get(booking.getId(), owner.getId());
        Assertions.assertEquals(booking.getItem(), dto.getItem());
        Assertions.assertEquals(booking.getId(), dto.getId());
        Assertions.assertEquals(booking.getStart(), dto.getStart());
        Assertions.assertEquals(booking.getState(), dto.getStatus());
        Assertions.assertEquals(booking.getEnd(), dto.getEnd());
    }

    @Test
    void getBookingsUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("User with ID=2 not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.get(user.getId()));
        assertEquals("User with ID=2 not found", e.getMessage());
    }

    @Test
    void getBookingsUserBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(anyLong()))
                .thenThrow(new NoSuchElementException("Booking not found"));
        final NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> bookingService.get(user.getId()));
        assertEquals("Booking not found", e.getMessage());
    }

    @Test
    void getBookings() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.get(user.getId());
        assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingsUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("User with ID=2 not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.getAllUserBookings(user.getId(), booking.getState().toString(),
                        1, 10));
        assertEquals("User with ID=2 not found", e.getMessage());
    }

    @Test
    void getAllUserBookingsFromMinus2() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        List<BookingDto> dtos = bookingService.getAllUserBookings(user.getId(), booking.getState().toString(), -2, 10);
        assertEquals(0, dtos.size());
    }

    @Test
    void getAllUserBookingsFromNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllUserBookings(user.getId(), booking.getState().toString(), -1, 10));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAllUserBookingsSizeNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllUserBookings(user.getId(), booking.getState().toString(), 1, -3));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAllUserBookingsSizeAndFromZeros() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllUserBookings(user.getId(), booking.getState().toString(), 0, 0));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAllUserBookingsAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking, bookingApproved, bookingCangelled, bookingRejected));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "ALL", 0, 10);
        assertEquals(4, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(1).getItem());
        Assertions.assertEquals(bookingCangelled.getItem(), bookings.get(2).getItem());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(3).getItem());
    }

    @Test
    void getAllUserBookingsApproved() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(bookingApproved));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "APPROVED", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingApproved.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingApproved.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingApproved.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingApproved.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingsRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "REJECTED", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingRejected.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingRejected.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingRejected.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingRejected.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingsWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "WAITING", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingsCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBefore(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingApproved, bookingCangelled));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "CURRENT", 0, 10);
        assertEquals(2, bookings.size());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingApproved.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingApproved.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingApproved.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingApproved.getEnd(), bookings.get(0).getEnd());
        Assertions.assertEquals(bookingCangelled.getItem(), bookings.get(1).getItem());
        Assertions.assertEquals(bookingCangelled.getId(), bookings.get(1).getId());
        Assertions.assertEquals(bookingCangelled.getStart(), bookings.get(1).getStart());
        Assertions.assertEquals(bookingCangelled.getState(), bookings.get(1).getStatus());
        Assertions.assertEquals(bookingCangelled.getEnd(), bookings.get(1).getEnd());
    }

    @Test
    void getAllUserBookingPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "PAST", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingRejected.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingRejected.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingRejected.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingRejected.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(),
                "FUTURE", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllUserBookingUnknown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final StateException e = assertThrows(StateException.class, () -> bookingService.getAllUserBookings(user.getId(),
                "ANY", 0, 10));
        assertEquals("UNKNOWN_STATE", e.getMessage());
    }

    @Test
    void getAllOwnerBookingsUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("User with ID=2 not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> bookingService.getAllOwnersBooking(user.getId(), booking.getState().toString(),
                        1, 10));
        assertEquals("User with ID=2 not found", e.getMessage());
    }

    @Test
    void getAllOwnerBookingsFromMinus2() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        List<BookingDto> dtos = bookingService.getAllOwnersBooking(user.getId(), booking.getState().toString(), -2, 10);
        assertEquals(0, dtos.size());
    }

    @Test
    void getAlOwnerBookingsFromNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllOwnersBooking(user.getId(), booking.getState().toString(), -1, 10));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAlOwnerBookingsSizeNegative() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllOwnersBooking(user.getId(), booking.getState().toString(), 1, -3));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAllOwnerBookingsSizeAndFromZeros() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class,
                () -> bookingService.getAllOwnersBooking(user.getId(), booking.getState().toString(), 0, 0));
        assertEquals("Invalid paging size", e.getMessage());
    }

    @Test
    void getAllOwnerBookingsAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking, bookingApproved, bookingCangelled, bookingRejected));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "ALL", 0, 10);
        assertEquals(4, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(1).getItem());
        Assertions.assertEquals(bookingCangelled.getItem(), bookings.get(2).getItem());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(3).getItem());
    }

    @Test
    void getAllOwnerBookingsApproved() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(bookingApproved));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "APPROVED", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingApproved.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingApproved.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingApproved.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingApproved.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllOwnerBookingsRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "REJECTED", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingRejected.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingRejected.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingRejected.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingRejected.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllOwnerBookingsWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndState(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "WAITING", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllOwnerBookingsCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndEndIsAfterAndStartIsBefore(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingApproved, bookingCangelled));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "CURRENT", 0, 10);
        assertEquals(2, bookings.size());
        Assertions.assertEquals(bookingApproved.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingApproved.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingApproved.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingApproved.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingApproved.getEnd(), bookings.get(0).getEnd());
        Assertions.assertEquals(bookingCangelled.getItem(), bookings.get(1).getItem());
        Assertions.assertEquals(bookingCangelled.getId(), bookings.get(1).getId());
        Assertions.assertEquals(bookingCangelled.getStart(), bookings.get(1).getStart());
        Assertions.assertEquals(bookingCangelled.getState(), bookings.get(1).getStatus());
        Assertions.assertEquals(bookingCangelled.getEnd(), bookings.get(1).getEnd());
    }

    @Test
    void getAllOwnerBookingPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "PAST", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(bookingRejected.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(bookingRejected.getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingRejected.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(bookingRejected.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(bookingRejected.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllOwnerBookingFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getAllOwnersBooking(user.getId(),
                "FUTURE", 0, 10);
        assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getItem(), bookings.get(0).getItem());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), bookings.get(0).getStart());
        Assertions.assertEquals(booking.getState(), bookings.get(0).getStatus());
        Assertions.assertEquals(booking.getEnd(), bookings.get(0).getEnd());
    }

    @Test
    void getAllOwnerBookingUnknown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        final StateException e = assertThrows(StateException.class, () -> bookingService.getAllOwnersBooking(user.getId(),
                "ANY", 0, 10));
        assertEquals("UNKNOWN_STATE", e.getMessage());
    }

    @Test
    void approveUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("User not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), true));
        assertEquals("User not found", e.getMessage());
    }

    @Test
    void approveBookingNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndId(anyLong(), anyLong()))
                .thenThrow(new NoSuchObjectException("Booking not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), true));
        assertEquals("Booking not found", e.getMessage());
    }

    @Test
    void approveBookingChangeStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndId(anyLong(), anyLong()))
                .thenReturn(bookingApproved);
        final ItemsAvailabilityException e = assertThrows(ItemsAvailabilityException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), true));
        assertEquals("Status cannot be changed", e.getMessage());
    }

    @Test
    void approveBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndId(anyLong(), anyLong()))
                .thenReturn(booking);
        BookingDto dto = bookingService.approve(owner.getId(), booking.getId(), true);
        assertEquals("APPROVED", booking.getState().toString());
    }

    @Test
    void rejectBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItem_OwnerIdAndId(anyLong(), anyLong()))
                .thenReturn(booking);
        BookingDto dto = bookingService.approve(owner.getId(), booking.getId(), false);
        assertEquals("REJECTED", booking.getState().toString());
    }
}
