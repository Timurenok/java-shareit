package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
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
        if (userRepository.findByEmail(user.getEmail()) != null) {
            userRepository.save(user);
            throw new UsedEmailException(String.format("User with email %s already exists", user.getEmail()));
        }
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User update(User user, Long id) {
        find(id);
        user.setId(id);
        if (user.getEmail() == null) {
            user.setEmail(userRepository.findById(id).get().getEmail());
        }
        if (user.getName() == null) {
            user.setName(userRepository.findById(id).get().getName());
            if (userRepository.findByEmail(user.getEmail()) != null
                    && !userRepository.findByEmail(user.getEmail()).getId().equals(id)) {
                throw new UsedEmailException(String.format("User with email %s already exists", user.getEmail()));
            }
        }
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User find(Long id) {
        return Optional.ofNullable(userRepository.findById(id).orElseThrow(() -> new UnknownUserException(
                String.format("User with id %d does not exist", id)))).get();
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User remove(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return user.get();
        }
        throw new UnknownUserException(String.format("User with id %d does not exist", id));
    }
}
