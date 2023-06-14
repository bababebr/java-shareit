package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto add(Long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NoSuchObjectException(String.format("Item with ID=%s not found", bookingDto.getItemId())));
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NoSuchObjectException(String.format("User with ID=%s not found", bookerId)));

        List<Booking> bookings = bookingRepository.findByItem_IdOrderByStartDesc(item.getId());

        if (bookerId.equals(item.getOwner().getId())) {
            throw new NoSuchObjectException("Booking cannot be done by owner.");
        }
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
        User owner = item.getOwner();
        Booking booking = BookingMapper.bookingDtoToBooking(bookingDto, owner, booker, item);
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchObjectException("Booking not found"));
        if (booking.getBooker().getId().longValue() == userId.longValue() ||
                booking.getOwner().getId().longValue() == userId.longValue()) {
            return BookingMapper.bookingToBookingDto(booking);
        } else {
            throw new NoSuchObjectException("Access denied.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> get(Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdOrderByStartDesc(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllUserBookings(Long userId, String status) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        switch (status) {
            case "ALL":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdOrderByStartDesc(userId));
            case "APPROVED":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId, BookingStatus.APPROVED));
            case "REJECTED":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId, BookingStatus.REJECTED));
            case "WAITING":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId, BookingStatus.WAITING));
            case "CURRENT":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now()));
            case "PAST":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()));
            default:
                throw new StateException("UNKNOWN_STATE");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllOwnersBooking(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        switch (state) {
            case "ALL":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdOrderByStartDesc(userId));
            case "APPROVED":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndState(userId, BookingStatus.APPROVED));
            case "REJECTED":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndState(userId, BookingStatus.REJECTED));
            case "WAITING":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndState(userId, BookingStatus.WAITING));
            case "CURRENT":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now()));
            case "PAST":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.bookingDtos(bookingRepository.findByOwner_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()));
            default:
                throw new StateException("UNKNOWN_STATE");
        }
    }

    @Override
    @Transactional
    public BookingDto approve(long ownerId, long bookingId, boolean approved) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchObjectException("User nof found"));
        Booking booking = bookingRepository.findByOwnerAndId(owner, bookingId);

        if (!booking.getState().equals(BookingStatus.WAITING)) {
            throw new ItemsAvailabilityException("Status cannot be changed");
        }
        if (approved) {
            booking.setState(BookingStatus.APPROVED);
        } else {
            booking.setState(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDto(booking);
    }
}
