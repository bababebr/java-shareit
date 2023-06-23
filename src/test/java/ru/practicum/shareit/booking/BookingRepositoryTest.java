package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;
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

    @PostConstruct
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
        bookingRejected = Booking.create(4L, item, user, startPast, startPast.plusMinutes(3), BookingStatus.REJECTED);
        bookingDto = BookingDto.create(1L, item.getId(), item, user, startFuture, end, BookingStatus.WAITING);
    }

    @Test
    @Transactional
    void findByItem_IdOrderByStartDesc() {

    }

    @Test
    void findByBooker_IdOrderByStartDesc() {
    }

    @Test
    void findByItem_OwnerIdOrderByStartDesc() {
    }

    @Test
    void findByBooker_IdAndState() {
    }

    @Test
    void findByItem_OwnerIdAndState() {
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findByItem_OwnerIdAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findByBooker_IdAndEndIsBeforeOrderByStartDesc() {
    }

    @Test
    void findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc() {
    }

    @Test
    void findByBooker_IdAndEndIsAfterAndStartIsBefore() {
    }

    @Test
    void findByItem_OwnerIdAndEndIsAfterAndStartIsBefore() {
    }

    @Test
    void findByItem_OwnerIdAndId() {
    }

    @Test
    void findByBookerAndItem() {
    }
}