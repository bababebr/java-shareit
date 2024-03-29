package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional()
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItem(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.dtoToRequest(itemRequestDto, requester));
        return ItemRequestMapper.requestToDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUsersAll(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterId(userId, PageRequest.of(from, size));
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (ItemRequest ir : itemRequests) {
            ItemRequestDto dto = ItemRequestMapper.requestToDto(itemRequests.get(from));
            if (ir.getItem() != null) {
                Item item = itemRepository.findById(ir.getItem().getId()).get();
                item.setRequestId(ir.getId());
                dto.getItems().add(item);
            }
            requestDtos.add(dto);
        }
        return requestDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(Long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchObjectException("Request has not found."));
        Item item = itemRepository.findById(itemRequest.getItem().getId()).get();
        item.setRequestId(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.requestToDto(itemRequest);
        itemRequestDto.getItems().add(item);
        return itemRequestDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOtherRequest(Long userId, int from, int size) {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdIsNot(userId, PageRequest.of(from, size));
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (ItemRequest ir : itemRequests) {
            ItemRequestDto dto = ItemRequestMapper.requestToDto(ir);
            if (ir.getItem() != null) {
                Item item = itemRepository.findById(ir.getItem().getId()).get();
                item.setRequestId(ir.getRequester().getId());
                dto.getItems().add(item);
            }
            requestDtos.add(dto);
        }
        return requestDtos;
    }
}
