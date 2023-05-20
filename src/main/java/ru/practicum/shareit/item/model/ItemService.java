package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId);

    ItemDto getItem(long itemId);

    List<ItemDto> getUsersOwnItems(long ownerId);

    List<ItemDto> searchItemByDescription(String searchText);
}