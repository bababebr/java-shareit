package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {

    @Autowired
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    RequestRepository requestRepository;
    @Autowired
    UserServiceImpl userService;
    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto ownerDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.create(1L, "Item 1", "Item 1", true, null);
        userDto = UserDto.create(2L, "User 1", "Email");
        ownerDto = UserDto.create(1L, "Owner 1", "Email");

    }

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
        userService.create(userDto);
        itemService.addItem(itemDto, userId);
        itemService.getItem(itemId, userId);
    }

    @Test
    void testUpdateItemFail() {
        ItemDto itemDto = ItemDto.create(999L, "Item 1", "Item 1", true, null);
        Assertions.assertThrows(NoSuchObjectException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
    }

    @Test
    void testGetAllItemsZero() {
        Assertions.assertEquals(itemService.getUsersOwnItems(4L).size(), 0);
    }

}

