package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.InvalidNameException;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.exception.UsedEmailException;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private static long id = 1;

    @Override
    public User save(User user) {
        if (user.getEmail() == null) {
            throw new InvalidEmailException("Email can't be empty");
        }
        if (user.getName() == null) {
            throw new InvalidNameException("Name can't be empty");
        }
        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException(String.format("Wrong format of email %s", user.getEmail()));
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UsedEmailException(String.format("User with email %s already exists", user.getEmail()));
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        user.setId(id);
        find(id);
        if (user.getEmail() == null) {
            user.setEmail(users.get(id).getEmail());
        }
        if (user.getName() == null) {
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail())
                    && u.getId() != user.getId())) {
                throw new UsedEmailException(String.format("User with email %s already exists", user.getEmail()));
            }
            user.setName(users.get(id).getName());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public User find(long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new UnknownUserException(
                String.format("User with id %d does not exist", id)));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User remove(long id) {
        return users.remove(id);
    }

    private long generateId() {
        return id++;
    }
}
