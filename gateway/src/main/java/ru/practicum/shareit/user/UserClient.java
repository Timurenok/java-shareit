package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> findUserById(Long id) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> findUsers() {
        return get("");
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Long id) {
        return patch("/" + id, userDto);
    }

    public ResponseEntity<Object> removeUser(Long id) {
        return delete("/" + id);
    }
}