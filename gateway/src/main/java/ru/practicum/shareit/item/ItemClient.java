package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> findItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findItemsByOwnerId(Long userId, Integer from, Integer size) {
        String path = "?from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> findItemsByText(String text, Integer from, Integer size) {
        String path = "/search?text=" + text + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path);
    }

    public ResponseEntity<Object> saveComment(CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}