package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookingRepositoryTest {
    @Autowired
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
        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        repository.saveAll(List.of(booking, bookingApproved, bookingCancelled, bookingRejected));
        List<Booking> result = repository.findByItem_IdOrderByStartDesc(item.getId());
        assertEquals(4, result.size());
    }

    @Test
    void findByBooker_IdOrderByStartDesc() {
        List<Booking> resultUser = repository.findByBooker_IdOrderByStartDesc(user.getId(), Pageable.unpaged());
        assertEquals(3, resultUser.size());
        List<Booking> resultOwner = repository.findByBooker_IdOrderByStartDesc(owner.getId(), Pageable.unpaged());
        assertEquals(1, resultOwner.size());
    }

    @Test
    void findByItem_OwnerIdOrderByStartDesc() {
        List<Booking> result = repository.findByItem_OwnerIdOrderByStartDesc(item.getOwner().getId(), Pageable.unpaged());
        assertEquals(4, result.size());
    }

    @Test
    void findByBooker_IdAndState() {
        List<Booking> result = repository.findByBooker_IdAndState(user.getId(),
                BookingStatus.APPROVED, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndState() {
        List<Booking> result = repository.findByItem_OwnerIdAndState(owner.getId(),
                BookingStatus.REJECTED, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByStartDesc() {
        List<Booking> result = repository.findByBooker_IdAndStartIsAfterOrderByStartDesc(user.getId(),
                startPast, Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> result = repository.findByItem_OwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(),
                startPast.minusHours(1), Pageable.unpaged());
        assertEquals(4, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByBooker_IdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> result = repository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(user.getId(),
                end.plusHours(1), Pageable.unpaged());
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> result = repository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                end.plusHours(1), Pageable.unpaged());
        assertEquals(0, result.size());
        result = repository.findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(),
                end.plusHours(1), Pageable.unpaged());
        assertEquals(4, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void findByBooker_IdAndEndIsAfterAndStartIsBefore() {
        List<Booking> result = repository.findByBooker_IdAndEndIsAfterAndStartIsBefore(owner.getId(),
                startPast.plusMinutes(2), startPast.plusMinutes(1),Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals(BookingStatus.REJECTED, result.get(0).getState());
    }

    @Test
    void findByItem_OwnerIdAndEndIsAfterAndStartIsBefore() {
        List<Booking> result = repository.findByItem_OwnerIdAndEndIsAfterAndStartIsBefore(owner.getId(),
                startPast.plusMinutes(2), startPast.plusMinutes(1),Pageable.unpaged());
        assertEquals(3, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals(BookingStatus.APPROVED, result.get(0).getState());
    }

    @Test
    void findByItem_OwnerIdAndId() {
        Booking result = repository.findByItem_OwnerIdAndId(owner.getId(), bookingApproved.getId());
        assertEquals(2, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getState());
        assertEquals(user.toString(), result.getBooker().toString());
    }

    @Test
    void findByBookerAndItem() {
        List<Booking> result = repository.findByBookerAndItem(owner.getId(), item.getId(), BookingStatus.REJECTED);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals(owner.toString(), result.get(0).getBooker().toString());
        assertEquals(BookingStatus.REJECTED, result.get(0).getState());
    }
}