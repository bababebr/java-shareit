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
@Transactional(readOnly = true)
public
class ItemServiceImpl implements ItemService {

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
            itemRequest.setItemId(item.getId());
            requestRepository.save(itemRequest);
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
    public ItemBookingHistoryDto getItem(long itemId, long userId) {
        Item item = repository.findById(itemId).orElseThrow(() ->
                new NoSuchObjectException(String.format("Item with ID=%s not found", itemId)));
        List<Booking> itemBookings = bookingRepository.findByItem_IdOrderByStartDesc(itemId);
        List<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        ItemBookingHistoryDto itemBookingHistoryDto = ItemMapper.itemBookingHistoryDto(item);
        if (item.getOwner().getId() == userId) {
            setBookings(itemBookingHistoryDto, itemBookings, item.getOwner());
        }
        setComments(itemBookingHistoryDto, itemComments);

        return itemBookingHistoryDto;
    }

    @Override
    public List<ItemBookingHistoryDto> getUsersOwnItems(long ownerId) {
        List<Item> items = repository.findItemsByOwner(ownerId);
        return items.stream().map(i -> getItem(i.getId(), ownerId)).collect(Collectors.toList());
    }

    @Override
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

    private void setBookings(ItemBookingHistoryDto item, List<Booking> bookings, User owner) {
        for (Booking booking : bookings) {
            if (booking.getItem().getOwner().getId().longValue() == owner.getId().longValue() &&
                    booking.getState() != BookingStatus.REJECTED) {
                //Find NextBooking
                for (Booking b : bookings) {
                    if (b.getStart().isBefore(LocalDateTime.now())) {
                        break;
                    }
                    item.setNextBooking(BookingMapper.bookingToBookingShort(b));
                }
                //Find Last Booking
                for (Booking b : bookings) {
                    if (b.getStart().isAfter(LocalDateTime.now())) {
                        continue;
                    }
                    item.setLastBooking(BookingMapper.bookingToBookingShort(b));
                    if (b.getStart().isBefore(LocalDateTime.now())) {
                        break;
                    }
                }
            }
        }
    }

    private void setComments(ItemBookingHistoryDto itemBookingHistoryDto, List<Comment> comments) {
        for (Comment c : comments) {
            User user = userRepository.findById(c.getAuthor().getId()).get();
            itemBookingHistoryDto.getComments().add(CommentMapper.commentToDto(c, user));
        }
    }
}