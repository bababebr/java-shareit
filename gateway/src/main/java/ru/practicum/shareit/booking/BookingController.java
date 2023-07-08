package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                      @Valid @RequestBody BookingDto booking) {
        return bookingClient.add(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(bookerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable long bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam (defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        return bookingClient.getAllOwnersBooking(userId, state, from, size);
    }
}
