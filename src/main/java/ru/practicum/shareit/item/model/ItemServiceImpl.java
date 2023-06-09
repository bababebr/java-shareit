package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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
        List<Booking> booking = bookingRepository.findItemsBooking(itemId);
        if (booking.isEmpty() || item.getUser().getId() != userId) {
            return ItemMapper.itemBookingHistoryDto(repository.getReferenceById(itemId));
        }
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = booking.get(0);
        Booking nextBooking = booking.get(0);
        for (Booking b : booking) {
            lastBooking = b.getEnd().isBefore(lastBooking.getEnd()) ? b : lastBooking;
            nextBooking = b.getStart().isAfter(now) ? b : nextBooking;
        }
        ItemBookingHistoryDto itemBookingHistoryDto = ItemMapper.itemBookingHistoryDto(repository.getReferenceById(itemId));
        itemBookingHistoryDto.setLastBooking(BookingMapper.bookingToBookingShort(lastBooking));
        itemBookingHistoryDto.setNextBooking(BookingMapper.bookingToBookingShort(nextBooking));

        List<Comment> comments = commentRepository.findAllByItem_id(itemId);
        itemBookingHistoryDto.getComments().addAll(comments);
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
    public CommentDTO addComment(Long itemId, long userId, CommentDTO commentDTO) {
        Item item = repository.findById(itemId).get();
        User user = userRepository.findById(userId).get();
        Comment comment = CommentMapper.DtoToComment(commentDTO, item, user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.commentToDto(commentRepository.save(comment));
    }
}