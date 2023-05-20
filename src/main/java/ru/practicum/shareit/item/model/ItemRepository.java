package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {

    ItemDto addItem(ItemDto item, User owner);

    ItemDto updateItem(ItemDto item, User owner, long itemId);

    ItemDto getItem(long itemId);

    List<ItemDto> getUsersOwnItems(long ownerId);

    List<ItemDto> searchItemByDescription(String searchText);

}