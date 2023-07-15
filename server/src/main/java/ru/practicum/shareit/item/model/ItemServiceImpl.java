package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NoSuchObjectException(String.format("There is no User with ID=%s.", ownerId)));
        Item item = repository.save(ItemMapper.dtoToItem(itemDto, user));
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId()).get();
            itemRequest.setItem(item);
            requestRepository.save(itemRequest);
            item.setRequestId(itemRequest.getId());
        }
        return ItemMapper.itemToDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        if (repository.existsById(itemId)) {
            Item oldItem = repository.findById(itemId).get();
            if (oldItem.getOwner().getId() != ownerId) {
                throw new NoSuchObjectException(String.format("User ID= %s doesn't have Item with ID=%s",
                        ownerId, itemId));
            }
            if (itemDto.getName() != null) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            repository.save(oldItem);
            return ItemMapper.itemToDto(oldItem);
        } else {
            throw new NoSuchObjectException(String.format("There is no Item with ID=%s.", itemId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingHistoryDto getItem(long itemId, long userId) {
        LocalDateTime requestTime = LocalDateTime.now();
        Item item = repository.findById(itemId).orElseThrow(() ->
                new NoSuchObjectException(String.format("Item with ID=%s not found", itemId)));
        List<Booking> itemBookings = bookingRepository.findByItem_IdOrderByStartDesc(itemId);
        List<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        ItemBookingHistoryDto itemBookingHistoryDto = ItemMapper.itemBookingHistoryDto(item);
        if (item.getOwner().getId() == userId) {
            setBookings(itemBookingHistoryDto, itemBookings, item.getOwner(), requestTime);
        }
        setComments(itemBookingHistoryDto, itemComments);

        return itemBookingHistoryDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingHistoryDto> getUsersOwnItems(long ownerId) {
        List<Item> items = repository.findItemsByOwner(ownerId);
        return items.stream().map(i -> getItem(i.getId(), ownerId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemByDescription(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = repository.findItemByNameAndDescription(searchText);
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO addComment(long itemId, long userId, CommentDTO commentDTO) {
        Item item = repository.findById(itemId).get();
        User user = userRepository.findById(userId).get();
        List<Booking> booking = bookingRepository.findByBookerAndItem(userId, itemId, BookingStatus.APPROVED);
        for (Booking b : booking) {
            if (b.getBooker().getId() == userId && b.getStart().isBefore(LocalDateTime.now())) {
                commentDTO.setCreated(LocalDateTime.now());
                Comment comment = CommentMapper.dtoToComment(commentDTO, item, user);
                comment.setCreated(LocalDateTime.now());
                return CommentMapper.commentToDto(commentRepository.save(comment), user);
            }
        }
        throw new CommentException(String.format("User with ID=?s didn't book item with ID=?s", userId, itemId));
    }

    private void setBookings(ItemBookingHistoryDto item, List<Booking> bookings, User owner, LocalDateTime r) {
        System.out.println(r + " - requset time");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");

        for (Booking booking : bookings) {
            if (booking.getItem().getOwner().getId().longValue() == owner.getId().longValue() &&
                    booking.getState() != BookingStatus.REJECTED) {
                System.out.println(booking.getId() + " is owned by " + owner.getId() + " and status OK");
                //Find NextBooking
                if (booking.getStart().isBefore(r) && booking.getEnd().isAfter(r)) {
                    System.out.println(booking.getId() + " start date is after " + r + " - Yes, next booking");
                    item.setNextBooking(BookingMapper.bookingToBookingShort(booking));
                }

                //Find Last Booking
                if (booking.getStart().isAfter(r) && booking.getEnd().isAfter(r)) {
                    System.out.println(booking.getId() + " start date is after " + r + " - passed");
                    item.setLastBooking(null);
                }
                System.out.println(booking.getId() + " start date is before " + r + " - Yes, last booking");
                item.setLastBooking(BookingMapper.bookingToBookingShort(booking));
            }
        }
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");
        System.out.println("//////////////////////////////");

    }

    private void setComments(ItemBookingHistoryDto itemBookingHistoryDto, List<Comment> comments) {
        for (Comment c : comments) {
            User user = userRepository.findById(c.getAuthor().getId()).get();
            itemBookingHistoryDto.getComments().add(CommentMapper.commentToDto(c, user));
        }
    }
}