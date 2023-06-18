package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserServiceImpl userService;

    @Test
    @Order(1)
    void testGetItemNotExist() {
        Long itemId = 1L;
        Long userId = 1L;
        Assertions.assertThrows(NoSuchObjectException.class, () -> itemService.getItem(itemId, userId));
    }

    @Order(2)
    @Test
    void testAddItem() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = ItemDto.create(999L, "Item 1", "Item 1", true, null);
        UserDto userDto = UserDto.create(2L, "User 1", "Email");

        userService.create(userDto);

        itemService.addItem(itemDto, userId);
        itemService.getItem(itemId, userId);
    }



}

