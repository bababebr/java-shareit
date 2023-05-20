package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {


    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        if (userRepository.isUserExist(ownerId)) {
            return repository.addItem(itemDto, userRepository.getUser(ownerId));
        }
        throw new NoSuchObjectException(String.format("There is no User with ID=%s.", ownerId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        if (userRepository.isUserExist(ownerId)) {
            return repository.updateItem(itemDto, userRepository.getUser(ownerId), itemId);
        }
        throw new NoSuchObjectException(String.format("There is no User with ID=%s.", ownerId));
    }

    @Override
    public ItemDto getItem(long itemId) {
        return repository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getUsersOwnItems(long ownerId) {
        if (userRepository.isUserExist(ownerId)) {
            return repository.getUsersOwnItems(ownerId);
        }
        throw new NoSuchObjectException(String.format("There is no User with ID=%s.", ownerId));
    }

    @Override
    public List<ItemDto> searchItemByDescription(String searchText) {
        return repository.searchItemByDescription(searchText);
    }
}