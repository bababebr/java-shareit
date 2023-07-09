package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemRequestDto itemRequestDto, Long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return post("", userId, parameters, itemRequestDto);
    }

    public ResponseEntity<Object> getUsersAll(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getOtherRequest(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "from", from,
                "size", size
        );
        return  get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> get(Long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "requestId", requestId
        );
        return get("/{requestId}", userId, parameters);
    }
}
