package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemsAvailabilityException;
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
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItem(ItemRequestDto itemRequestDto, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setUserId(userId);
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.DtoToRequest(itemRequestDto, userId));
        return ItemRequestMapper.requestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUsersAll(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        List<ItemRequest> itemRequests = requestRepository.findAllByUserId(userId);
        for (ItemRequest i : requestRepository.findAll()) {
            System.out.println(i);
        }
        System.out.println(itemRequests.size());
        if (from == -2) {
            return new ArrayList<>();
        }
        if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
            throw new ItemsAvailabilityException("Invalid paging size");
        }
        if (itemRequests.isEmpty()) {
            System.out.println("here");
            return new ArrayList<>();
        }
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        int maxSize = itemRequests.size();
        maxSize = maxSize > size ? size : maxSize;
        int step = 0;
        while (step < maxSize) {
            ItemRequestDto dto = ItemRequestMapper.requestToDto(itemRequests.get(from));
            if (itemRequests.get(from).getItemId() != null) {
                Item item = itemRepository.findById(itemRequests.get(from).getItemId()).get();
                dto.getItems().add(item);
            }
            requestDtos.add(dto);
            from++;
            step++;
        }
        return requestDtos;
    }

    @Override
    public ItemRequestDto get(Long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchObjectException("Request has not found."));
        ItemRequestDto itemRequestDto = ItemRequestMapper.requestToDto(itemRequest);
        itemRequestDto.getItems().add(itemRepository.findById(itemRequest.getItemId()).get());
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getOtherRequest(Long userId, int from, int size) {

        if (from == -2) {
            return new ArrayList<>();
        }
        List<ItemRequest> itemRequests = requestRepository.findAllForOtherUsers(userId);
        if ((from < 0 || size < 0) || (from == 0 && size == 0)) {
            throw new ItemsAvailabilityException("Invalid paging size");
        }

        List<ItemRequestDto> requestDtos = new ArrayList<>();
        int maxSize = itemRequests.size();
        maxSize = maxSize > size ? size : maxSize;
        int step = 0;
        while (step < maxSize) {
            ItemRequestDto dto = ItemRequestMapper.requestToDto(itemRequests.get(from));
            if (itemRequests.get(from).getItemId() != null) {
                Item item = itemRepository.findById(itemRequests.get(from).getItemId()).get();
                dto.getItems().add(item);
            }
            requestDtos.add(dto);
            from++;
            step++;
        }
        return requestDtos;
    }
}
