package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.create(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>());
        return itemRequestDto;
    }

    public static ItemRequest dtoToRequest(ItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = ItemRequest.create(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated(),
                requester,
                null);
        return itemRequest;
    }

}
