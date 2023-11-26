package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User saveUser(@Validated @RequestBody User user) {
        log.info("Creating user {}", user);
        return userService.save(user);
    }

    @PatchMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable long id) {
        log.info("Updating user {}", user);
        return userService.update(user, id);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable long id) {
        log.info("Getting user with id {}", id);
        return userService.find(id);
    }

    @GetMapping
    public List<User> findALl() {
        log.info("Getting all users");
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    public User removeUser(@PathVariable long id) {
        log.info("Removing user with id {}", id);
        return userService.remove(id);
    }
}
