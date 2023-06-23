package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;

@DataJpaTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    private User owner = User.create(1L, "owner", "owner@mail.ru");
    private User owner2 = User.create(2L, "owner", "owner2@mail.ru");
    private Item item = Item.create(1L, owner, true, "item 1", "item 1", null);

    @BeforeEach
    void setUp() {

    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void findItemsByOwner() {

    }

}