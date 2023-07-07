package ru.practicum.shareit.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BaseClientTest {

    @Mock
    BaseClient client;
    @Autowired
    UserRepository repository;
    @Autowired
    UserService userService;

    private RestTemplate rest = new RestTemplate();

    private MockRestServiceServer mockServer;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void get() throws JsonProcessingException, URISyntaxException {
        User emp = User.create(1L, "name", "email");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/users/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(emp))
                );
        ResponseEntity<Object> resp = rest.getForEntity("http://localhost:8080/users/1", Object.class);
        Mockito.when(client.get(anyString(), anyLong(), anyMap()))
                .thenReturn(resp);
        Mockito.when(client.get(anyString(), anyLong()))
                .thenReturn(resp);
        Mockito.when(client.get(anyString()))
                .thenReturn(resp);
        Object o = client.get("http://localhost:8080/users/1").getBody();
    }

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(rest);
    }

    @Test
    void testGet() {
    }

    @Test
    void testGet1() {
    }

    @Test
    void post() {
    }

    @Test
    void testPost() {
    }

    @Test
    void testPost1() {
    }

    @Test
    void put() {
    }

    @Test
    void testPut() {
    }

    @Test
    void patch() {
    }

    @Test
    void testPatch() {
    }

    @Test
    void testPatch1() {
    }

    @Test
    void testPatch2() {
    }

    @Test
    void delete() {
    }

    @Test
    void testDelete() {
    }

    @Test
    void testDelete1() {
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}