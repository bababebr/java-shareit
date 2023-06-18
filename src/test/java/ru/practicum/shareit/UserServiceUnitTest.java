package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {


    @Autowired
    UserServiceImpl userService;
    @Mock
    UserRepository mockUserRepository;

    @Test
    void testAddUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("1");
        userDto.setEmail("email");

        UserDto userDto1= userService.create(userDto);

        assertSame(userDto1.getId(), userService.get(1L).getId());
        assertSame(userDto1.getName(), userService.get(1L).getName());
        assertSame(userDto1.getEmail(), userService.get(1L).getEmail());
    }
    @Test
    void testGetUserNotFound() {
        userService = new UserServiceImpl(mockUserRepository);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenThrow(new NoSuchObjectException("User with ID not found"));

        final NoSuchObjectException exception = assertThrows(
                NoSuchObjectException.class,
                () -> userService.get(1L));
        assertEquals("User with ID not found", exception.getMessage());
    }

    @Test
    void testCreateWithWrongEmail() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        UserDto userDto = UserDto.create(1L, "1", "email@google.com");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new ValidationException("Email cannot be null or empty."));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.create(userDto));
        assertEquals("Email cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testUpdateUserNotExist(){
        assertThrows(NoSuchElementException.class, () -> userService.update(new UserDto(), 999L));
    }

}
