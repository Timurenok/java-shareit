package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private final UserMapper userMapper;

    private final User user = new User(null, "user", "user@gmail.com");

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.save(user);
        assertThat(returnUserDto.getName(), equalTo(user.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldExceptionWhenDeleteUserWithWrongId() {
        UnknownUserException exception = assertThrows(UnknownUserException.class, () -> userService.remove(10L));
        assertEquals("User with id 10 does not exist", exception.getMessage());
    }

    @Test
    void shouldUpdateUser() {
        UserDto returnUserDto = userService.save(user);
        returnUserDto.setName("new");
        returnUserDto.setEmail("new@gmail.com");
        userService.update(userMapper.userDtoToUser(returnUserDto), returnUserDto.getId());
        UserDto updateUserDto = userService.find(returnUserDto.getId());
        assertThat(updateUserDto.getName(), equalTo("new"));
        assertThat(updateUserDto.getEmail(), equalTo("new@gmail.com"));
    }

    @Test
    void shouldDeleteUser() {
        User newUser = new User(null, "newUser", "newUser@gmail.com");
        UserDto returnUserDto = userService.save(newUser);
        List<UserDto> listUser = userService.findAll();
        int size = listUser.size();
        userService.remove(returnUserDto.getId());
        listUser = userService.findAll();
        assertThat(listUser.size(), equalTo(size - 1));
    }
}
