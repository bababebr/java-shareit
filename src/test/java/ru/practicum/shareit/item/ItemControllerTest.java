package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ItemDto itemDto1True;
    private ItemDto itemDto2False;
    private ItemDto itemDto3True;
    private UserDto userDto1 = UserDto.create(1L, "user 1", "user 1");
    private UserDto userDto2 = UserDto.create(2L, "user 2", "user 2");
    private User user1 = User.create(1L, "user 1", "user 1");
    private User user2 = User.create(2L, "user 2", "user 2");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
        itemDto1True = ItemDto.create(1L, "item 1", "item 1", true, null);
        itemDto2False = ItemDto.create(2L, "item 2", "item 2", false, null);
        itemDto3True = ItemDto.create(3L, "item 3", "item 3", true, null);
    }

    @Test
    void add() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto1True);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(ItemMapper.dtoToItem(itemDto1True, user1));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1True))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1True.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1True.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1True.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1True.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }

    @Test
    void update() {
    }

    @Test
    void getUsersOwnItems() {
    }

    @Test
    void get() {
    }

    @Test
    void search() {
    }

    @Test
    void addComment() {
    }
}