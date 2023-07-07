package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@DataJpaTest
class BookingRepositoryTest {
    @Mock
    private BookingRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Booking booking;
    private Booking bookingCancelled;
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
        user = User.create(1L, "user", "user@mail.ru");
        owner = User.create(2L, "owner", "owner@mail.ru");
        item = Item.create(1L, owner, true, "Item 1", "Item", null);
        startFuture = LocalDateTime.now().plusHours(1);
        startPast = LocalDateTime.now().minusHours(1);
        end = LocalDateTime.now().plusHours(2);
        booking = Booking.create(1L, item, user, startFuture, end, BookingStatus.WAITING);
        bookingApproved = Booking.create(2L, item, user, startPast, end, BookingStatus.APPROVED);
        bookingCancelled = Booking.create(3L, item, user, startPast, end, BookingStatus.CANCELLED);
        bookingRejected = Booking.create(4L, item, owner, startPast, startPast.plusMinutes(3), BookingStatus.REJECTED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, startFuture, end, BookingStatus.WAITING);
    }

    @Test
    void findByItem_IdOrderByStartDesc() {

        Mockito.when(repository.findByItem_IdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled, bookingRejected));
        List<Booking> result = repository.findByItem_IdOrderByStartDesc(item.getId());
        assertEquals(4, result.size());
    }

    @Test
    void findByBooker_IdOrderByStartDesc() {
        Mockito.when(repository.findByBooker_IdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled));
        List<Booking> resultUser = repository.findByBooker_IdOrderByStartDesc(user.getId(), Pageable.unpaged());
        assertEquals(3, resultUser.size());
    }

    @Test
    void findByItem_OwnerIdOrderByStartDesc() {
        Mockito.when(repository.findByItem_OwnerIdOrderByStartDesc(item.getOwner().getId(), Pageable.unpaged()))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled, bookingRejected));
        List<Booking> result = repository.findByItem_OwnerIdOrderByStartDesc(item.getOwner().getId(), Pageable.unpaged());
        assertEquals(4, result.size());
    }

    @Test
    void findByBooker_IdAndState() {
        Mockito.when(repository.findByBooker_IdAndState(user.getId(),
                        BookingStatus.APPROVED, Pageable.unpaged()))
                .thenReturn(List.of(bookingApproved));
        List<Booking> result = repository.findByBooker_IdAndState(user.getId(),
                BookingStatus.APPROVED, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndState() {
        Mockito.when(repository.findByItem_OwnerIdAndState(owner.getId(),
                        BookingStatus.REJECTED, Pageable.unpaged()))
                .thenReturn(List.of(bookingRejected));
        List<Booking> result = repository.findByItem_OwnerIdAndState(owner.getId(),
                BookingStatus.REJECTED, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByStartDesc() {
        Mockito.when(repository.findByBooker_IdAndStartIsAfterOrderByStartDesc(user.getId(),
                        startPast, Pageable.unpaged()))
                .thenReturn(List.of(bookingRejected));
        List<Booking> result = repository.findByBooker_IdAndStartIsAfterOrderByStartDesc(user.getId(),
                startPast, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndStartIsAfterOrderByStartDesc() {
        Mockito.when(repository.findByItem_OwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(),
                        startPast.minusHours(1), Pageable.unpaged()))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled, bookingRejected));
        List<Booking> result = repository.findByItem_OwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(),
                startPast.minusHours(1), Pageable.unpaged());
        assertEquals(4, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByBooker_IdAndEndIsBeforeOrderByStartDesc() {
        Mockito.when(repository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(user.getId(),
                        end.plusHours(1), Pageable.unpaged()))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled));
        List<Booking> result = repository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(user.getId(),
                end.plusHours(1), Pageable.unpaged());
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc() {
        Mockito.when(repository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                        end.plusHours(1), Pageable.unpaged()))
                .thenReturn(List.of());
        List<Booking> result = repository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                end.plusHours(1), Pageable.unpaged());
        assertEquals(0, result.size());
    }

    @Test
    void findByBooker_IdAndEndIsAfterAndStartIsBefore() {
        Mockito.when(repository.findByBooker_IdAndEndIsAfterAndStartIsBefore(owner.getId(),
                        startPast.plusMinutes(2), startPast.plusMinutes(1), Pageable.unpaged()))
                .thenReturn(List.of(bookingRejected));
        List<Booking> result = repository.findByBooker_IdAndEndIsAfterAndStartIsBefore(owner.getId(),
                startPast.plusMinutes(2), startPast.plusMinutes(1), Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals(BookingStatus.REJECTED, result.get(0).getState());
    }

    @Test
    void findByItem_OwnerIdAndEndIsAfterAndStartIsBefore() {
        Mockito.when(repository.findByItem_OwnerIdAndEndIsAfterAndStartIsBefore(owner.getId(),
                        startPast.plusMinutes(2), startPast.plusMinutes(1), Pageable.unpaged()))
                .thenReturn(List.of(booking, bookingApproved, bookingCancelled));
        List<Booking> result = repository.findByItem_OwnerIdAndEndIsAfterAndStartIsBefore(owner.getId(),
                startPast.plusMinutes(2), startPast.plusMinutes(1), Pageable.unpaged());
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(BookingStatus.WAITING, result.get(0).getState());
    }

    @Test
    void findByItem_OwnerIdAndId() {
        Mockito.when(repository.findByItem_OwnerIdAndId(owner.getId(), bookingApproved.getId()))
                .thenReturn(bookingApproved);
        Booking result = repository.findByItem_OwnerIdAndId(owner.getId(), bookingApproved.getId());
        assertEquals(2, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getState());
        assertEquals(user.toString(), result.getBooker().toString());
    }

    @Test
    void findByBookerAndItem() {
        Mockito.when(repository.findByBookerAndItem(owner.getId(), item.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of(bookingRejected));
        List<Booking> result = repository.findByBookerAndItem(owner.getId(), item.getId(), BookingStatus.REJECTED);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals(owner.toString(), result.get(0).getBooker().toString());
        assertEquals(BookingStatus.REJECTED, result.get(0).getState());
    }
}