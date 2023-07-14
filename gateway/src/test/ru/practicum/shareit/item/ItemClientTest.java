package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(ItemClient.class)
@ActiveProfiles("test")
class ItemClientTest {

    @Autowired
    ItemClient itemClient;

    @Autowired
    private MockRestServiceServer server;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void add() throws JsonProcessingException {
        ItemDto itemDto = ItemDto.create(1L, "Item 1", "", true, 1L);
        this.server.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess(mapper.writeValueAsString(itemDto.getName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.add(1L, itemDto);
        assertEquals(response.getBody().toString(), "Item 1");
    }

    @Test
    void update() throws JsonProcessingException {
        ItemDto itemDto = ItemDto.create(1L, "Item 1", "", true, 1L);
        this.server.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(itemDto.getName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.update(1L, 1L, itemDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().toString(), "Item 1");
    }

    @Test
    void getItems() throws JsonProcessingException {
        ItemDto itemDto = ItemDto.create(1L, "Item 1", "", true, 1L);
        this.server.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(itemDto.getName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.getItems(1L, 1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().toString(), "Item 1");
    }

    @Test
    void getOwnerItems() throws JsonProcessingException {
        ItemDto itemDto = ItemDto.create(1L, "Item 1", "", true, 1L);
        this.server.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess(mapper.writeValueAsString(itemDto.getName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.getOwnerItems(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().toString(), "Item 1");
    }

    @Test
    void search() throws JsonProcessingException {
        ItemDto itemDto = ItemDto.create(1L, "Item 1", "", true, 1L);
        this.server.expect(requestTo("http://localhost:9090/items/search?text=item"))
                .andRespond(withSuccess(mapper.writeValueAsString(itemDto.getName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.search(1L, "item");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().toString(), "Item 1");
    }

    @Test
    void addComment() throws JsonProcessingException {
        CommentDTO commentDTO = CommentDTO.create(1L, "text", "name", LocalDateTime.now());
        this.server.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andRespond(withSuccess(mapper.writeValueAsString(commentDTO.getAuthorName()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = itemClient.addComment(1L, 1L, commentDTO);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().toString(), "name");
    }
}