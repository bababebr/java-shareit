package ru.practicum.shareit.UserTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private UserDto userDto = UserDto.create(1L,
            "User 1",
            "user@mail.ru");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void create() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void createWrongEmail() throws Exception {
        when(userService.create(any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void getAllUsers() throws Exception {
        ArrayList<UserDto> result = new ArrayList<>();
        result.add(userDto);
        result.add(userDto);
        when(userService.getAll())
                .thenReturn(result);
        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllUsersEmpty() throws Exception {
        ArrayList<UserDto> result = new ArrayList<>();
        when(userService.getAll())
                .thenReturn(result);
        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUser() throws Exception {
        Long userId = userDto.getId();
        when(userService.get(userId))
                .thenReturn(userDto);
        mvc.perform(get("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void getUserNotFound() throws Exception {
        when(userService.get(999L))
                .thenThrow(NoSuchObjectException.class);
        mvc.perform(get("/users/{userId}", 999)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void update() throws Exception {
        UserDto updatedUser = UserDto.create(1L, "updatedName", "updatedEmail");
        when(userService.update(any(UserDto.class), anyLong()))
                .thenReturn(updatedUser);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail()), String.class));
    }

    @Test
    void delete() throws Exception {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        UserDto userDto2 = UserDto.create(2L, "User 2", "email2@mail.ru");
        when(userService.create(any()))
                .thenAnswer(invocationOnMock -> {
                    userDtos.add(invocationOnMock.getArgument(0, UserDto.class));
                    return invocationOnMock.getArgument(0);
                });
        //add two Users
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(userDtos.size(), 2);
        //Delete userDto2
        when(userService.delete(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    userDtos.removeIf(u -> u.getId() == invocationOnMock.getArgument(0, Long.class));
                    return userDto;
                });

        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", 2L)
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
        Assertions.assertEquals(userDtos.size(), 1);

    }
}