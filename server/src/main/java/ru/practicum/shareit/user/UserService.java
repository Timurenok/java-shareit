package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(User user);

    UserDto update(User user, Long id);

    UserDto find(Long id);

    List<UserDto> findAll();

    UserDto remove(Long id);
}
