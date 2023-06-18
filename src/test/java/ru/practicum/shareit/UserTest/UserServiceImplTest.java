package ru.practicum.shareit.UserTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;

import javax.transaction.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Autowired
    UserServiceImpl userService;
    @Mock
    UserRepository mockUserRepository;

    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(mockUserRepository);
        owner = User.create(1L, "owner", "owner@mail.ru");
        booker = User.create(2L, "booker", "booker@mail.ru");
        item1 = Item.create(1L, owner, true, "item 1", "item 1", null);
        item2 = Item.create(2L, booker, true, "item 2", "item 2", null);
    }

    @Test
    void getAll() {
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(List.of(owner, booker));
        ArrayList<User> list = new ArrayList<>(UserMapper.userDtoToUser(userService.getAll()));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void get() {
        Mockito.when(mockUserRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Assertions.assertEquals(owner.getName(), userService.get(owner.getId()).getName());
    }

    @Test
    void getUserNotFound() {
        Mockito.when(mockUserRepository.findById(999L))
                .thenThrow(new NoSuchElementException());

        final NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> userService.get(999L));
        assertEquals(new NoSuchElementException().getLocalizedMessage(), exception.getLocalizedMessage());
    }

    @Test
    void create() {
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(owner);
        assertEquals(owner.getEmail(),userService.create(UserMapper.userToDto(owner)).getEmail());
        assertEquals(owner.getId(),userService.create(UserMapper.userToDto(owner)).getId());
        assertEquals(owner.getName(),userService.create(UserMapper.userToDto(owner)).getName());
    }

    @Test
    void update() {
        UserDto updatedBooker = UserDto.create(booker.getId(), "updatedBooker", booker.getEmail());
        Mockito.when(mockUserRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                        .then(invocationOnMock -> {
                            User user = invocationOnMock.getArgument(0, User.class);

                            booker.setId(user.getId());
                            booker.setEmail(user.getEmail());
                            booker.setName(user.getName());
                            return booker;
                        });
        userService.update(updatedBooker, booker.getId());
        assertEquals(booker.getEmail(),updatedBooker.getEmail());
        assertEquals(booker.getId(),updatedBooker.getId());
        assertEquals(booker.getName(),updatedBooker.getName());;
    }

    @Test
    void delete() {


    }
}