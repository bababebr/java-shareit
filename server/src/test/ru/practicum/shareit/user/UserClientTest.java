package ru.practicum.shareit.user;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(UserClient.class)
@ActiveProfiles("test")
class UserClientTest {

    @Autowired
    UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getUser() throws JsonProcessingException  {
        User user = User.create(1L, "name", "email");
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(mapper.writeValueAsString(user)), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = userClient.getUser(1L);
        assertEquals("email", user.getEmail());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void addUser() throws JsonProcessingException {
        User user = User.create(1L, "name", "email");
        UserDto userDto = UserDto.create(1L, "name", "email");
        this.server.expect(requestTo("http://localhost:9090/users"))
                .andRespond(withSuccess(mapper.writeValueAsString(mapper.writeValueAsString(user)), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = userClient.addUser(userDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void updateUser() throws JsonProcessingException {
        User user = User.create(1L, "name", "email");
        UserDto userDto = UserDto.create(1L, "name", "email");
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(mapper.writeValueAsString(user)), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = userClient.updateUser(1L, userDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void delete() throws JsonProcessingException {
        User user = User.create(1L, "name", "email");
        UserDto userDto = UserDto.create(1L, "name", "email");
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(mapper.writeValueAsString(user)), MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = userClient.delete(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}