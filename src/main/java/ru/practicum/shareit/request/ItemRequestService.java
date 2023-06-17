package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItem(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUsersAll(Long userId, int from, int size);

    ItemRequestDto get(long requestId);

    List<ItemRequestDto> getOtherRequest(Long userId, int from, int size);
}
