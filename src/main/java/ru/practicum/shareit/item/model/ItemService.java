package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId);

    ItemBookingHistoryDto getItem(long itemId, long userId);

    List<ItemBookingHistoryDto> getUsersOwnItems(long ownerId);

    List<ItemDto> searchItemByDescription(String searchText);

    CommentDTO addComment(long itemId, long userId, CommentDTO commentDTO);
}