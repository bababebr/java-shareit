package ru.practicum.shareit.item;

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
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(long userId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return post("", userId, parameters, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, long itemId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "itemId", itemId
        );
        return patch("/{itemId}", userId, parameters, itemDto);
    }

    public ResponseEntity<Object> getItems(long userId, long itemId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "itemId", itemId
        );
        return get("/{itemId}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerItems(long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> search(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "userId", userId
        );
        return get("?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, Long itemId, CommentDTO commentDTO) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId,
                "userId", userId
        );
        return post("/{itemId}/comment", userId, parameters, commentDTO);
    }
}
