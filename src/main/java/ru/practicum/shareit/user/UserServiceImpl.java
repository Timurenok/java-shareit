package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.InvalidNameException;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.exception.UsedEmailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto save(User user) {
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
        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(User user, Long id) {
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
        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto find(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(() -> new UnknownUserException(
                String.format("User with id %d does not exist", id))));
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::userToUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto remove(Long id) {
        UserDto userDto = find(id);
        userRepository.deleteById(id);
        return userDto;
    }
}
