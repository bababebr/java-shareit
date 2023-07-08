package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    RequestClient mockItemRequestService;
    ObjectMapper mapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private User owner;
    private User booker;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        owner = User.create(1L, "owner", "owner@mail.ru");
        booker = User.create(2L, "booker", "booker@mail.ru");
        item = Item.create(1L, owner, true, "desc", "item 1", null);
        itemRequestDto = ItemRequestDto.create(1L, "desc", created, List.of(item));
        itemRequest = ItemRequest.create(1L, "desc", created, owner, item);
    }

    @Test
    void add() throws Exception {
        when(mockItemRequestService.addItem(any(ItemRequestDto.class), anyLong()))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.items.length()", is(1)));
    }

    @Test
    void getUsersAll() throws Exception {
        when(mockItemRequestService.getUsersAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));
        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)))
    }

    @Test
    void getOtherRequest() throws Exception {
        when(mockItemRequestService.getOtherRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));
        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$[0].items.length()", is(1)));
    }

    @Test
    void get() throws Exception {
        when(mockItemRequestService.get(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));
        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter)), LocalDateTime.class))
                .andExpect(jsonPath("$.items.length()", is(1)));
    }
}