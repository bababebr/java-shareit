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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NoSuchObjectException(String.format("There is no User with ID=%s.", ownerId)));
        Item item = repository.save(ItemMapper.dtoToItem(itemDto, user));
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {

        if (repository.existsById(itemId)) {
            Item oldItem = repository.findById(itemId).get();
            if (oldItem.getUser().getId() != ownerId) {
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
        Item item = repository.findById(itemId).orElseThrow(() ->
                new NoSuchObjectException(String.format("Item with ID=%s not found", itemId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException(""));
        List<Booking> itemBookings = bookingRepository.findItemsBooking(itemId);
        List<Comment> itemComments = commentRepository.findAllByItem_id(itemId);
        ItemBookingHistoryDto itemBookingHistoryDto = ItemMapper.itemBookingHistoryDto(item);
        LocalDateTime now = LocalDateTime.now();

        for(Booking b : itemBookings) {
            if(b.getOwner().getId() == userId) {
                Booking lastBooking = itemBookings.get(0);
                Booking nextBooking = itemBookings.get(0);
                for (Booking b1 : itemBookings) {
                    lastBooking = b1.getEnd().isBefore(lastBooking.getEnd()) ? b1 : lastBooking;
                    nextBooking = b1.getStart().isAfter(now) ? b1 : nextBooking;
                }
                itemBookingHistoryDto.setLastBooking(BookingMapper.bookingToBookingShort(lastBooking));
                itemBookingHistoryDto.setNextBooking(BookingMapper.bookingToBookingShort(nextBooking));
            }
        }
        for(Comment c : itemComments) {
            User userC = userRepository.findById(c.getAuthor_id()).get();
                itemBookingHistoryDto.getComments().add(CommentMapper.commentToDto(c, userC));
        }

        return itemBookingHistoryDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingHistoryDto> getUsersOwnItems(long ownerId) {
        List<Item> items = repository.findItemsByUserId(ownerId);
        List<ItemBookingHistoryDto> itemBookingHistoryDtos = items.stream().map(i -> getItem(i.getId(), ownerId)).collect(Collectors.toList());
        return itemBookingHistoryDtos;
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
    public CommentDTO addComment(long itemId, long userId, CommentDTO commentDTO) {
        Item item = repository.findById(itemId).get();
        User user = userRepository.findById(userId).get();
        List<Booking> booking = bookingRepository.findBookingByBookerAndItemAndStateOrderByStartDesc(userId, itemId, BookingStatus.APPROVED);
        LocalDateTime now = LocalDateTime.now();
        for (Booking b : booking) {
            if (b.getBooker().getId() == userId && b.getStart().isBefore(now)) {
                Comment comment = CommentMapper.DtoToComment(commentDTO, item, user);
                comment.setCreated(now);
                return CommentMapper.commentToDto(commentRepository.save(comment), user);
            }
        }
        throw new CommentException(String.format("User with ID=?s didn't book item with ID=?s", userId, itemId));
    }
}