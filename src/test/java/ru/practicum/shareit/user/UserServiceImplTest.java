package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
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
    void shouldThrowExceptionWhenGetUserWithWrongId() {
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