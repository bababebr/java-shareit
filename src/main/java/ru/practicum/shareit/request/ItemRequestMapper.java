package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto requestToDto(ItemRequest itemRequest, List<Item> itemsList) {
        ItemRequestDto itemRequestDto = ItemRequestDto.create(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemsList,
                itemRequest.getUserId());
        return itemRequestDto;
    }

    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.create(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>(),
                itemRequest.getUserId());
        return itemRequestDto;
    }

    public static ItemRequest dtoToRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequest.create(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated(),
                userId,
                null);
        return itemRequest;
    }

}
