package ru.practicum.shareit.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class BaseClientTest {

    @InjectMocks
    BaseClient baseClient;
    @Mock
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void get() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.OK));
        ResponseEntity<Object> o = baseClient.get("users/1");
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        o = baseClient.get("users/1", 1L);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        final NullPointerException e = assertThrows(NullPointerException.class,
                () -> baseClient.get("users/1", 1L, param));
        assertEquals(new NullPointerException().getMessage(), e.getMessage());
    }

    @Test
    void post() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.OK));
        ResponseEntity<Object> o = baseClient.post("users/1", user);
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        o = baseClient.post("users/1", 1L, user);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        final NullPointerException e = assertThrows(NullPointerException.class,
                () -> baseClient.patch("users/1", 1L, param));
        assertEquals(e.getClass(), NullPointerException.class);

    }

    @Test
    void put() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.OK));
        ResponseEntity<Object> o = baseClient.put("users/1", 1L, user);
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        o = baseClient.put("users/1", 1L, param);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
    }

    @Test
    void patch() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.OK));
        ResponseEntity<Object> o = baseClient.patch("users/1", 1L);
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        final NullPointerException e = assertThrows(NullPointerException.class,
                () -> baseClient.patch("users/1", 1L, param));
        assertEquals(e.getClass(), NullPointerException.class);

        o = baseClient.patch("", user);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());

    }

    @Test
    void delete() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.OK));
        ResponseEntity<Object> o = baseClient.delete("users/1");
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        o = baseClient.delete("users/1", 1L);
        assertEquals(o.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), responseUser.getId());
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getEmail(), responseUser.getEmail());
        final NullPointerException e = assertThrows(NullPointerException.class,
                () -> baseClient.delete("users/1", 1L, param));
        assertEquals(new NullPointerException().getMessage(), e.getMessage());
    }

    @Test
    void httpStatusException() {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new RestClientException(HttpStatus.NOT_ACCEPTABLE.toString()));
        final RestClientException e = assertThrows(RestClientException.class,
                () -> baseClient.get(""));
        assertEquals(e.getMessage(), HttpStatus.NOT_ACCEPTABLE.toString());
    }

    @Test
    void getNotOk() throws JsonProcessingException {
        HashMap<String, Object> param = new HashMap<>();
        User user = User.create(1L, "name", "email");
        param.put("1", user);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(mapper.writeValueAsString(user), HttpStatus.NOT_FOUND));
        ResponseEntity<Object> o = baseClient.get("users/1");
        User responseUser = mapper.readValue(o.getBody().toString(), User.class);
        assertEquals(o.getStatusCode(), HttpStatus.NOT_FOUND);

    }

}