package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.InvalidNameException;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;

    private UserService userService;

    private final UserDto userDto = new UserDto(1L, "user", "user@gmail.com");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        when(mockUserRepository.save(any()))
                .thenThrow(new InvalidEmailException("Email can't be empty"));
        final InvalidEmailException exception = Assertions.assertThrows(
                InvalidEmailException.class,
                () -> userService.save(new User(1L, "user", null)));
        Assertions.assertEquals("Email can't be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        when(mockUserRepository.save(any()))
                .thenThrow(new InvalidNameException("Name can't be empty"));
        final InvalidNameException exception = Assertions.assertThrows(
                InvalidNameException.class,
                () -> userService.save(new User(1L, null, "user@gmail.com")));
        Assertions.assertEquals("Name can't be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsIncorrect() {
        when(mockUserRepository.save(any()))
                .thenThrow(new InvalidEmailException("Wrong format of email userGmail.com"));
        final InvalidEmailException exception = Assertions.assertThrows(
                InvalidEmailException.class,
                () -> userService.save(new User(1L, "user", "userGmail.com")));
        Assertions.assertEquals("Wrong format of email userGmail.com", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFindUserWithWrongId() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UnknownUserException exception = Assertions.assertThrows(
                UnknownUserException.class,
                () -> userService.find(100L));
        Assertions.assertEquals("User with id 100 does not exist", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithExistEmail() {
        when(mockUserRepository.save(any()))
                .thenThrow(new InvalidUserException("User with email user@gmail.com already exists"));
        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userService.save(UserMapper.mapToUser(userDto)));
        Assertions.assertEquals("User with email user@gmail.com already exists", exception.getMessage());
    }

    @Test
    void shouldReturnUserWhenFindUserById() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(UserMapper.mapToUser(userDto)));
        User user = UserMapper.mapToUser(userService.find(1L));
        verify(mockUserRepository, Mockito.times(1))
                .findById(1L);
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}