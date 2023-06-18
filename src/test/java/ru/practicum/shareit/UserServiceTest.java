package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {


    @Autowired
    UserServiceImpl userService;

    @Test
    void testAddUserSuccess() {
        UserServiceImpl mockUserService = Mockito.mock(UserServiceImpl.class);
        UserDto userDto = UserDto.create(1L, "1", "email@google.com");
        userService.create(userDto);

        Assertions.assertSame(userDto.getId(), userService.get(1L).getId());
        Assertions.assertSame(userDto.getName(), userService.get(1L).getName());
        Assertions.assertSame(userDto.getEmail(), userService.get(1L).getEmail());
    }

    @Test
    void testAddUserWrongEmail() {
        UserServiceImpl mockUserService = Mockito.mock(UserServiceImpl.class);
        UserDto userDto = UserDto.create(1L, "1", "email@google.com");
        userService.create(userDto);

        Assertions.assertSame(userDto.getEmail(), userService.get(1L).getEmail());
    }
}
