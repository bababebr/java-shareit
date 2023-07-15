package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(RequestClient.class)
@ActiveProfiles("test")
class RequestClientTest {
    @Autowired
    RequestClient requestClient;

    @Autowired
    private MockRestServiceServer server;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void addItem() throws JsonProcessingException {
        ItemRequestDto requestDto = ItemRequestDto.create(1L, "", LocalDateTime.now(), List.of());
        this.server.expect(requestTo("http://localhost:9090/requests"))
                .andRespond(withSuccess(mapper.writeValueAsString(requestDto.getId()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = requestClient.addItem(requestDto, 1L);
        assertEquals(response.getBody().toString(), "1");
    }

    @Test
    void getUsersAll() throws JsonProcessingException {
        ItemRequestDto requestDto = ItemRequestDto.create(1L, "", LocalDateTime.now(), List.of());
        this.server.expect(requestTo("http://localhost:9090/requests?from=0&size=10"))
                .andRespond(withSuccess(mapper.writeValueAsString(requestDto.getId()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = requestClient.getUsersAll(1L, 0, 10);
        assertEquals(response.getBody().toString(), "1");
    }

    @Test
    void getOtherRequest() throws JsonProcessingException {
        ItemRequestDto requestDto = ItemRequestDto.create(1L, "", LocalDateTime.now(), List.of());
        this.server.expect(requestTo("http://localhost:9090/requests/all?from=0&size=10"))
                .andRespond(withSuccess(mapper.writeValueAsString(requestDto.getId()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = requestClient.getOtherRequest(1L, 0, 10);
        assertEquals(response.getBody().toString(), "1");
    }

    @Test
    void get() throws JsonProcessingException {
        ItemRequestDto requestDto = ItemRequestDto.create(1L, "", LocalDateTime.now(), List.of());
        this.server.expect(requestTo("http://localhost:9090/requests"))
                .andRespond(withSuccess(mapper.writeValueAsString(requestDto.getId()), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = requestClient.get("");
        assertEquals(response.getBody().toString(), "1");
    }
}