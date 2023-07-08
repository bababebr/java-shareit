package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {


/*    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Valid @RequestBody BookingDto booking) {
        return bookingService.add(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                              @PathVariable long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(bookerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long bookingId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") @Min(0)  int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam (defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        return bookingService.getAllOwnersBooking(userId, state, from, size);
    }*/
}
