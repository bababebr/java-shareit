package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    private Item itemOwner;
    private Item itemUser;
    private User owner;
    private User user;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        owner = User.create(1L, "owner", "owner@mail.ru");
        user = User.create(2L, "user", "user@mail.ru");
        itemOwner = Item.create(1L, owner, true, "Item 1", "hammer", null);
        itemUser = Item.create(2L, user, true, "Item 2", "axe", null);
    }

    @Test
    void findItemsByOwner() {
        List<Item> items = repository.findItemsByOwner(user.getId());
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemOwner.getId());
    }

    @Test
    void findItemByNameAndDescription() {
        List<Item> items = repository.findItemByNameAndDescription("Item");
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), itemOwner.getId());
    }
}