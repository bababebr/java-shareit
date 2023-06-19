package ru.practicum.shareit.UserTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

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
                .andExpect(jsonPath("$.length()").value(2));
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
    void update() {
        
    }

    @Test
    void delete() {
    }
}