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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.exception.NoSuchObjectException;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemBookingHistoryDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ItemDto itemDto1True;
    private ItemDto itemDto2False;
    private ItemDto itemDto3True;

    private Item item1;
    private Item item2;
    private UserDto userDto1 = UserDto.create(1L, "user 1", "user 1");
    private UserDto userDto2 = UserDto.create(2L, "user 2", "user 2");
    private User user1 = User.create(1L, "user 1", "user 1");
    private User user2 = User.create(2L, "user 2", "user 2");

    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
        itemDto1True = ItemDto.create(1L, "item 1", "item 1", true, null);
        itemDto2False = ItemDto.create(2L, "item 2", "item 2", false, null);
        itemDto3True = ItemDto.create(3L, "item 3", "item 3", true, null);

        item1 = Item.create(1L, user1, true, itemDto1True.getDescription(),
                itemDto1True.getName(), itemDto1True.getRequestId());
        item2 = Item.create(itemDto2False.getId(), user2, itemDto2False.getAvailable(),
                itemDto2False.getDescription(), itemDto2False.getName(), itemDto2False.getRequestId());
        commentDTO = CommentDTO.create(1L, "text", user1.getName(), LocalDateTime.now());
        mapper.findAndRegisterModules();
    }

    @Test
    void add() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto1True);
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
    void addUserNotFound() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong()))
                .thenThrow(new NoSuchObjectException("There is no User with ID=1L."));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1True))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void update() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto2False);
        mvc.perform(patch("/items/{itemId}", itemDto2False.getId())
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .content(mapper.writeValueAsString(itemDto2False))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2False.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2False.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto2False.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto2False.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }

    @Test
    void getUsersOwnItems() throws Exception {
        when(itemService.getUsersOwnItems(anyLong()))
                .thenReturn(List.of(ItemBookingHistoryDto.create(1L, item1.getName(), item1.getDescription(),
                        item1.getAvailable(), null, null, null, null)));
        mvc.perform(MockMvcRequestBuilders.get("/items/")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1True))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void get() throws Exception {
        ItemBookingHistoryDto dto = ItemBookingHistoryDto.create(1L, item1.getName(), item1.getDescription(),
                item1.getAvailable(), null, null, null, null);
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(dto);

        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", item1.getId())
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(dto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId").doesNotExist())
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist());
    }

    @Test
    void search() throws Exception {
        when(itemService.searchItemByDescription(anyString()))
                .thenReturn(List.of(itemDto3True, itemDto1True));
        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDTO.class)))
                .thenReturn(commentDTO);
        mvc.perform(post("/items/{itemId}/comment", item1.getId())
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDTO.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDTO.getText()), String.class))
                .andExpect(jsonPath("$.created", is(commentDTO.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnn'Z'"))), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDTO.getAuthorName()), Boolean.class));
    }
}