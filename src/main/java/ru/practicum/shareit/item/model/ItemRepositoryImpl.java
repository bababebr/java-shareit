package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Resource(name = "memoryItemRepository")
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, ArrayList<Long>> userOwnItems = new HashMap<>();

    @Override
    public ItemDto addItem(ItemDto itemDto, User owner) {
        Item item = ItemMapper.dtoToItem(itemDto, owner);
        item.setId(getId());
        items.put(item.getId(), item);

        ArrayList<Long> ownItemsId = userOwnItems.get(owner.getId());
        if (ownItemsId == null) {
            ownItemsId = new ArrayList<>();
            ownItemsId.add(item.getId());
            userOwnItems.put(owner.getId(), ownItemsId);
        } else {
            userOwnItems.get(owner.getId()).add(item.getId());
        }
        return ItemMapper.itemToDto(item);
    }


    @Override
    public ItemDto updateItem(ItemDto itemDto, User owner, long itemId) {
        if (items.get(itemId) != null && items.get(itemId).getOwner().getId().equals(owner.getId())) {
            Item item = ItemMapper.dtoToItem(itemDto, owner);
            Item oldItem = items.get(itemId);
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            return ItemMapper.itemToDto(oldItem);
        }
        throw new NoSuchObjectException(String.format("Unable to update Item. " +
                "There is no Item with ID=%s.", itemId));
    }

    @Override
    public ItemDto getItem(long itemId) {
        Optional<Item> itemOpt = Optional.ofNullable(items.get(itemId));
        if (itemOpt.isPresent()) {
            return ItemMapper.itemToDto(itemOpt.get());
        }
        throw new NoSuchObjectException(String.format("There is no Item with ID=%s.", itemId));
    }

    @Override
    public List<ItemDto> getUsersOwnItems(long ownerId) {
        ArrayList<Long> itemsIdList = userOwnItems.get(ownerId);
        if (itemsIdList == null) {
            return new ArrayList<>();
        } else {
            return userOwnItems.get(ownerId).stream()
                    .map(itemId -> ItemMapper.itemToDto(items.get(itemId)))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ItemDto> searchItemByDescription(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        HashSet<String> keyWords = Stream.of(searchText.replaceAll("\\s{2,}", " ")
                .replaceAll("//W{1,}", " ")
                .trim()
                .toLowerCase()
                .split(" ")).filter(kw -> kw.length() >= 4).collect(Collectors.toCollection(HashSet::new));

        List<ItemDto> searchResult = new ArrayList<>();
        for (Item item : items.values().stream().filter(Item::getAvailable).collect(Collectors.toList())) {
            if (keyWords.stream()
                    .allMatch(kw -> item.getName().toLowerCase().contains(kw) ||
                            item.getDescription().toLowerCase().contains(kw))) {
                searchResult.add(ItemMapper.itemToDto(item));
            }
        }

        if (searchResult.isEmpty()) {
            return new ArrayList<>();
        } else {
            return searchResult;
        }
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}