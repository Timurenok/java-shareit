package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User save(User user);

    User update(User user, Long id);

    User find(Long id);

    List<User> findAll();

    User remove(Long id);
}
