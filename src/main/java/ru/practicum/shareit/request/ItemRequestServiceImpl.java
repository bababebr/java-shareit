package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
        requestRepository.save(ItemRequestMapper.DtoToRequest(itemRequestDto, userId));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getUsersAll(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchObjectException("User has not found."));
        List<ItemRequest> itemRequests = requestRepository.findAllByUserId(userId);
        if(itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        return null;
    }

    @Override
    public ItemRequestDto get(long requestId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getOtherRequest(Long userId, Integer from, Integer size) {
        return null;
    }
}
