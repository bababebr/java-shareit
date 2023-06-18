package ru.practicum.shareit.UserTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserMapperUnitTest {

    private User user;
    private User user2;
    private UserDto userDto;
    private UserDto userDto2;
    private ArrayList<User> userArrayList;
    private ArrayList<UserDto> userDtosArrayList;

    @BeforeEach
    void setUp() {
        user = User.create(1L, "user", "user@email.ru");
        userDto = UserDto.create(1L, "user", "user@email.ru");
        user2 = User.create(2L, "user2", "user2@email.ru");
        userDto2 = UserDto.create(2L, "user2", "user2@email.ru");
        userArrayList = new ArrayList<>(UserMapper.userDtoToUser(List.of(userDto, userDto2)));
        userDtosArrayList = new ArrayList<>(UserMapper.userToDto(List.of(user, user2)));
    }

    @Test
    void testUserToDto() {
        UserDto newDto = UserMapper.userToDto(user);
        Assertions.assertEquals(userDto.getName(), newDto.getName());
        Assertions.assertEquals(userDto.getId(), newDto.getId());
        Assertions.assertEquals(userDto.getEmail(), newDto.getEmail());
    }

    @Test
    void testDtoToUser() {
        User newUser = UserMapper.userDtoToUser(userDto);
        Assertions.assertEquals(user.getId(), newUser.getId());
        Assertions.assertEquals(user.getName(), newUser.getName());
        Assertions.assertEquals(user.getEmail(), newUser.getEmail());
    }

    @Test
    void testDtosToUsers() {
        ArrayList<User> testList = new ArrayList<>(UserMapper.userDtoToUser(List.of(userDto, userDto2)));
        for (int i = 0; i < testList.size(); i++) {
            Assertions.assertEquals(userArrayList.get(i).getId(), testList.get(i).getId());
            Assertions.assertEquals(userArrayList.get(i).getName(), testList.get(i).getName());
            Assertions.assertEquals(userArrayList.get(i).getEmail(), testList.get(i).getEmail());
        }
    }

    @Test
    void testUsersToDtos() {
        ArrayList<UserDto> testList = new ArrayList<>(UserMapper.userToDto(List.of(user, user2)));
        for (int i = 0; i < testList.size(); i++) {
            Assertions.assertEquals(userDtosArrayList.get(i).getId(), testList.get(i).getId());
            Assertions.assertEquals(userDtosArrayList.get(i).getName(), testList.get(i).getName());
            Assertions.assertEquals(userDtosArrayList.get(i).getEmail(), testList.get(i).getEmail());
        }
    }
}
