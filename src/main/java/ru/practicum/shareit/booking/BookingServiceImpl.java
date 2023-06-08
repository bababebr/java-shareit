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
        if (!item.getAvailable()) {
            throw new ItemsAvailabilityException(
                    String.format("Item with ID=%s is not available", bookingDto.getItemId())
            );
        }
        User owner = item.getUser();
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NoSuchObjectException(String.format("User with ID=%s not found", bookerId)));
        bookingDto.setState(BookingStatus.WAITING);
        bookingDto.setBookerId(bookerId);
        Booking booking = BookingMapper.bookingDtoToBooking(bookingDto, owner, booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public BookingDto update(Long ownerId, Long bookingId, BookingDto bookingDto, BookingStatus state) {
        return null;
    }

    @Override
    public List<BookingDto> get(Long bookingId, Long bookerId) {
        return null;
    }

}
