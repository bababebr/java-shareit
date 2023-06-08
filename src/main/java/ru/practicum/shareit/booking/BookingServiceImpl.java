package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto add(Long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NoSuchObjectException(String.format("Item with ID=%s not found", bookingDto.getItemId())));
        List<Booking> bookings = bookingRepository.findBookingsByItem_Id(item.getId());

        if (!item.getAvailable()) {
            throw new ItemsAvailabilityException(
                    String.format("Item with ID=%s is not available.", bookingDto.getItemId())
            );
        }

        for (Booking b : bookings) {
            if ((bookingDto.getStart().isBefore(b.getEnd()) && bookingDto.getStart().isAfter(b.getStart())) ||
                    bookingDto.getEnd().isAfter(b.getStart()) && bookingDto.getEnd().isBefore(b.getEnd())) {
                throw new ItemsAvailabilityException(
                        String.format("Item with ID=%s is booked for this period.", bookingDto.getItemId())
                );

            }
        }

        User owner = item.getUser();
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NoSuchObjectException(String.format("User with ID=%s not found", bookerId)));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(booker);
        Booking booking = BookingMapper.bookingDtoToBooking(bookingDto, owner, booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public BookingDto update(Long ownerId, Long bookingId, BookingDto bookingDto, BookingStatus state) {
        return null;
    }

    @Override
    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchObjectException("Booking not found"));
        if (booking.getBooker().getId() == userId || booking.getOwner().getId() == userId) {
            return BookingMapper.bookingToBookingDto(booking);
        }
        return null;
    }

    @Override
    public BookingDto approve(long bookerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchObjectException("Booking not found"));
        Item item = itemRepository.findById(booking.getItem().getId()).get();
        if (bookerId == item.getUser().getId()) {
            if (approved) {
                booking.setState(BookingStatus.APPROVED);
            } else {
                booking.setState(BookingStatus.REJECTED);
            }
        }
        bookingRepository.save(booking);
        itemRepository.save(item);
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllUsersBooking(Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        return BookingMapper.bookingDtos(bookingRepository.findBookingsByBooker_Id(userId));
    }

    @Override
    public List<BookingDto> getAllOwnersBooking(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        if(state == null) {
            state = "ALL";
        }
        return BookingMapper.bookingDtos(bookingRepository.findBookingsByOwner_IdAndState(userId, state));
    }
}
