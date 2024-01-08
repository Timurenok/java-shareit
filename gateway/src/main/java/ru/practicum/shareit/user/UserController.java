package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.saveUser(userDto);
    }

    @ResponseBody
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Updating user {}", userDto);
        return userClient.updateUser(userDto, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Long id) {
        log.info("Getting user with id {}", id);
        return userClient.findUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findUsers() {
        log.info("Getting all users");
        return userClient.findUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeUser(@PathVariable Long id) {
        log.info("Removing user with id {}", id);
        return userClient.removeUser(id);
    }
}