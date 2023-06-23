package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User booker;
    private User booker2;
    private Item item;
    private LocalDateTime created;
    private ItemRequest request;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        booker = User.create(1L, "booker", "booker@mail.ru");
        booker2 = User.create(4L, "booker2", "booker2@mail.ru");
        item = Item.create(1L, booker, true, "desc", "item 1", null);
        request = ItemRequest.create(1L, "request 1", created, booker, item);
        request2 = ItemRequest.create(2L, "request 2", created, booker2, item);
    }

    @Test
    void addRequest() {
    }

    @Test
    void findAllByRequesterId() {

    }

    @Test
    void findAllByRequesterIdIsNot() {

    }
}