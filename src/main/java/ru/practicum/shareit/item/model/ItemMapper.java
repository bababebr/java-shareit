package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto itemToDto(Item item) {
        return ItemDto.create(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }

    public static ItemBookingHistoryDto itemBookingHistoryDto(Item item) {
        return ItemBookingHistoryDto.create(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                item.getRequestId());
    }

    public static Item dtoToItem(ItemDto itemDto, User owner) {
        return Item.create(itemDto.getId(),
                owner,
                itemDto.getAvailable(),
                itemDto.getDescription(),
                itemDto.getName(),
                itemDto.getRequestId());
    }
}