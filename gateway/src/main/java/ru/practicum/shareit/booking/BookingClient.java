package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(Long bookerId, BookingDto booking) {
        Map<String, Object> parameters = Map.of(
                "bookerId", bookerId
        );
        return post("", bookerId, parameters, booking);
    }

    public ResponseEntity<Object> approve(Long bookerId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookerId", bookerId,
                "bookingId", bookingId,
                "approved", approved
        );
        return post("?approved={approved}", bookerId, parameters);
    }

    public ResponseEntity<Object> get(Long userId, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "userId", userId
        );
        return get("/{bookingId}", userId, parameters);

    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllOwnersBooking(Long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}", userId, parameters);
    }
}
