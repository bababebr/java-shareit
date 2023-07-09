package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(BookingClient.class)
@DirtiesContext
class BookingClientTest {
    @Autowired
    BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer server;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void add() {
        this.server.expect(requestTo("http://localhost:9090/bookings"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        User user = User.create(1L, "name", "email");
        Item item = Item.create(1L, user, true, "", "", 1L);
        BookingDto bookingDto = BookingDto.create(1L, 1L, item, user, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        ResponseEntity<Object> response = bookingClient.add(1L, bookingDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void approve() throws JsonProcessingException {
        User user = User.create(1L, "name", "email");
        Item item = Item.create(1L, user, true, "", "", 1L);
        BookingDto bookingDto = BookingDto.create(1L, 1L, item, user, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        this.server.expect(requestTo("http://localhost:9090/bookings/1?approved=true"))
                .andRespond(withSuccess(mapper.writeValueAsString(item), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approve(1L, user.getId(), true);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void get() {
        User user = User.create(1L, "name", "email");
        Item item = Item.create(1L, user, true, "", "", 1L);
        BookingDto bookingDto = BookingDto.create(1L, 1L, item, user, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        this.server.expect(requestTo("http://localhost:9090/bookings/1"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = bookingClient.get(1L, user.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void getUserBookings() {
        User user = User.create(1L, "name", "email");
        Item item = Item.create(1L, user, true, "", "", 1L);
        BookingDto bookingDto = BookingDto.create(1L, 1L, item, user, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        this.server.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=10"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = bookingClient.getUserBookings(1L, "ALL", 0, 10);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void getAllOwnersBooking() {
        User user = User.create(1L, "name", "email");
        Item item = Item.create(1L, user, true, "", "", 1L);
        BookingDto bookingDto = BookingDto.create(1L, 1L, item, user, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        this.server.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL&from=0&size=10"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = bookingClient.getAllOwnersBooking(1L, "ALL", 0, 10);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}