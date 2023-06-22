package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        Booking booking = BookingMapper.bookingDtoToBooking(bookingDto, booker, item);
        bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchObjectException("Booking not found"));
        if (booking.getBooker().getId().longValue() == userId.longValue() ||
                booking.getItem().getOwner().getId().longValue() == userId.longValue()) {
            return BookingMapper.bookingToBookingDto(booking);
        } else {
            throw new NoSuchObjectException("Access denied.");
        }
    }

    @Override
    public List<BookingDto> get(Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        return BookingMapper.bookingDtos(bookingRepository.findByBooker_IdOrderByStartDesc(userId, Pageable.unpaged()));
    }

    @Override
    public List<BookingDto> getAllUserBookings(Long userId, String status, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        List<BookingDto> bookingDtos;
        switch (status) {
            case "ALL":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdOrderByStartDesc(userId,
                        Pageable.unpaged()));
                if((bookingDtos.size() - from) < size) {
                    size = bookingDtos.size() - from;
                }
                pageRequest = PageRequest.of(from, size);
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdOrderByStartDesc(userId,
                        pageRequest));
                return bookingDtos;
            case "APPROVED":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId,
                        BookingStatus.APPROVED, pageRequest));
                return bookingDtos;
            case "REJECTED":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId
                        , BookingStatus.REJECTED, pageRequest));
                return bookingDtos;
            case "WAITING":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndState(userId,
                        BookingStatus.WAITING, pageRequest));
                return bookingDtos;
            case "CURRENT":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBefore(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                return bookingDtos;
            case "PAST":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                return bookingDtos;
            case "FUTURE":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                return bookingDtos;
            default:
                throw new StateException("UNKNOWN_STATE");
        }
    }

    @Override
    public List<BookingDto> getAllOwnersBooking(Long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(()
                -> new NoSuchObjectException(String.format("User with ID=%s not found", userId)));
        List<BookingDto> bookingDtos;
        PageRequest pageRequest = PageRequest.of(from, size);
        switch (state) {
            case "ALL":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByItem_OwnerIdOrderByStartDesc(userId,
                        pageRequest));
                return bookingDtos;
            case "APPROVED":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByItem_OwnerIdAndState(userId,
                        BookingStatus.APPROVED, pageRequest));
                return bookingDtos;
            case "REJECTED":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByItem_OwnerIdAndState(userId,
                        BookingStatus.REJECTED, pageRequest));
                return bookingDtos;
            case "WAITING":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByItem_OwnerIdAndState(userId,
                        BookingStatus.WAITING, pageRequest));
                return bookingDtos;
            case "CURRENT":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.findByItem_OwnerIdAndEndIsAfterAndStartIsBefore(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                return bookingDtos;
            case "PAST":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.
                        findByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest));
                return bookingDtos;
            case "FUTURE":
                bookingDtos = BookingMapper.bookingDtos(bookingRepository.
                        findByItem_OwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest));
                return bookingDtos;
            default:
                throw new StateException("UNKNOWN_STATE");
        }
    }

    @Override
    @Transactional
    public BookingDto approve(long ownerId, long bookingId, boolean approved) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchObjectException("User nof found"));
        Booking booking = bookingRepository.findByItem_OwnerIdAndId(owner.getId(), bookingId);

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
