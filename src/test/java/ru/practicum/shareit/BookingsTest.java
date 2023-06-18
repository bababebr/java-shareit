package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class BookingsTest {

    @Autowired
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    private Booking booking;
    private Item item;
    private User user;
    private User owner;
    @BeforeEach
    void setUp(){
        user = User.create(1L, "user", "user@mail.ru");
        owner = User.create(2L, "owner", "owner@mail.ru");
        item = Item.create(1L, owner, true, "Item 1", "Item", null);
        booking = Booking.create(1L, item, user, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), BookingStatus.WAITING);
    }

    @Test
    void addBooking() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Booking retrunBooking = bookingRepository.save(booking);
        Assertions.assertEquals(booking.getItem(), retrunBooking.getItem());
        Assertions.assertEquals(booking.getId(), retrunBooking.getId());
        Assertions.assertEquals(booking.getStart(), retrunBooking.getStart());
        Assertions.assertEquals(booking.getState(), retrunBooking.getState());
        Assertions.assertEquals(booking.getEnd(), retrunBooking.getEnd());
    }

    @Test
    void getBookingWithWrongId() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenThrow(new NoSuchObjectException("Booking not found"));

        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> bookingService.get(booking.getId(), user.getId()));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingWithWrongUser() {
        Mockito.when(bookingRepository.findByBooker_IdAndState(Mockito.anyLong(), Mockito.any(BookingStatus.class)))
                .thenThrow(NoSuchObjectException.class);
        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> bookingService.getAllUserBookings(user.getId(),"APPROVED" ,0, 0));
        assertEquals("User with ID=1 not found", exception.getMessage());
    }

}
