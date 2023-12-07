package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;

    private final UserService userService;
    private final UserDto user = new UserDto(null, "user", "user@gmail.com");
    private final UserDto newUser = new UserDto(null, "newUser", "newUser@gmail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(100L, "description",
            user, LocalDateTime.now(), null);

    @Test
    void shouldCreateItemRequest() {
        UserDto userDto = userService.save(UserMapper.mapToUser(user));
        ItemRequestDto returnRequestDto = itemRequestService.save(userDto.getId(), itemRequestDto);
        assertThat(returnRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void shouldThrowExceptionWhenCreateItemRequestWithWrongUserId() {
        UnknownUserException exception = assertThrows(UnknownUserException.class,
                () -> itemRequestService.save(100L, itemRequestDto));
        assertEquals("User with id 100 does not exist", exception.getMessage());
    }

    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        UserDto firstUserDto = userService.save(UserMapper.mapToUser(user));
        UnknownUserException exception = assertThrows(UnknownUserException.class,
                () -> itemRequestService.findById(100L, firstUserDto.getId()));
        assertEquals("User with id 100 does not exist", exception.getMessage());
    }

    @Test
    void shouldReturnItemRequestsWhenSizeNotNull() {
        UserDto userDto = userService.save(UserMapper.mapToUser(user));
        UserDto newUserDto = userService.save(UserMapper.mapToUser(newUser));
        itemRequestService.save(userDto.getId(), itemRequestDto);
        itemRequestService.save(newUserDto.getId(), itemRequestDto);
        List<ItemRequestDto> listItemRequest = itemRequestService.findByPages(userDto.getId(), 0, 10);
        assertThat(listItemRequest.size(), equalTo(1));
    }

    @Test
    void shouldReturnItemRequestsWhenSizeNull() {
        UserDto userDto = userService.save(UserMapper.mapToUser(user));
        UserDto newUserDto = userService.save(UserMapper.mapToUser(newUser));
        itemRequestService.save(userDto.getId(), itemRequestDto);
        itemRequestService.save(newUserDto.getId(), itemRequestDto);
        List<ItemRequestDto> listItemRequest = itemRequestService.findByPages(userDto.getId(), 0, null);
        assertThat(listItemRequest.size(), equalTo(1));
    }

    @Test
    void shouldReturnItemRequestById() {
        UserDto userDto = userService.save(UserMapper.mapToUser(user));
        ItemRequestDto newItemRequestDto = itemRequestService.save(userDto.getId(), itemRequestDto);
        ItemRequestDto returnItemRequestDto = itemRequestService.findById(userDto.getId(), newItemRequestDto.getId());
        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}
