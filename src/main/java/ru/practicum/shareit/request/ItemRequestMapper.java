package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto requestToDto(ItemRequest itemRequest, List<Item> itemsList) {
        ItemRequestDto itemRequestDto = ItemRequestDto.create(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemsList);
        return itemRequestDto;
    }

    public static ItemRequest DtoToRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequest.create(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated(),
                userId);
        return itemRequest;
    }

}
