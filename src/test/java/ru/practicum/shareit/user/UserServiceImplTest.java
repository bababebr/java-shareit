package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.model.Item;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Autowired
    UserServiceImpl userService;
    @Mock
    UserRepository mockUserRepository;

    private Item item1;
    private Item item2;
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
        when(mockUserRepository.findAll())
                .thenReturn(List.of(owner, booker));
        ArrayList<User> list = new ArrayList<>(UserMapper.userDtoToUser(userService.getAll()));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void get() {
        when(mockUserRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Assertions.assertEquals(owner.getName(), userService.get(owner.getId()).getName());
    }

    @Test
    void getUserNotFound() {
        when(mockUserRepository.findById(999L))
                .thenThrow(new NoSuchElementException());

        final NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> userService.get(999L));
        assertEquals(new NoSuchElementException().getLocalizedMessage(), exception.getLocalizedMessage());
    }

    @Test
    void create() {
        when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(owner);
        assertEquals(owner.getEmail(),userService.create(UserMapper.userToDto(owner)).getEmail());
        assertEquals(owner.getId(),userService.create(UserMapper.userToDto(owner)).getId());
        assertEquals(owner.getName(),userService.create(UserMapper.userToDto(owner)).getName());
    }

    @Test
    void update() {
        UserDto updatedBooker = UserDto.create(booker.getId(), "updatedBooker", booker.getEmail());
        when(mockUserRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        when(mockUserRepository.save(Mockito.any(User.class)))
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
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        UserDto dto = userService.delete(owner.getId());
        assertEquals(owner.getId(), dto.getId());
        assertEquals(owner.getName(), dto.getName());
        assertEquals(owner.getEmail(), dto.getEmail());
    }

    @Test
    void deleteNotFound() {
        when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NoSuchObjectException("User not found"));
        final NoSuchObjectException e = assertThrows(NoSuchObjectException.class,
                () -> userService.delete(owner.getId()));
        assertEquals(e.getMessage(), "User not found");
    }
}