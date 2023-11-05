package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User save(User user);

    User update(User user, long id);

    User find(long id);

    List<User> findAll();

    User remove(long id);
}
