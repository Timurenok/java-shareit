package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private final User user = new User(null, "user", "user@gmail.com");

    private final User newUser = new User(null, "newUser", "newUser@gmail.com");

    private final ItemDto itemDto = new ItemDto(null, "item", "description", true,
            1L, null, null, null, null);

    private final ItemDto newItemDto = new ItemDto(null, "newItem", "newDescription", true,
            2L, null, null, null, null);

    @Test
    void shouldCreateItem() {
        UserDto userDto = userService.save(user);
        ItemDto returnItemDto = itemService.save(itemMapper.mapToItem(itemDto), userDto.getId());
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void shouldThrowExceptionWhenUpdateItemByUserNotOwner() {
        UserDto ownerDto = userService.save(user);
        UserDto newUserDto = userService.save(newUser);
        ItemDto returnItemDto = itemService.save(itemMapper.mapToItem(itemDto), ownerDto.getId());
        UnknownUserException exception = assertThrows(UnknownUserException.class,
                () -> itemService.update(returnItemDto.getId(), itemMapper.mapToItem(newItemDto), newUserDto.getId()));
        assertEquals("You don't have an ability to change this item", exception.getMessage());
    }

    @Test
    void shouldUpdateItem() {
        UserDto userDto = userService.save(user);
        ItemDto returnItemDto = itemService.save(itemMapper.mapToItem(itemDto), userDto.getId());
        returnItemDto.setName("newItem");
        returnItemDto.setDescription("newDescription");
        returnItemDto.setAvailable(false);
        returnItemDto = itemService.update(returnItemDto.getId(), itemMapper.mapToItem(returnItemDto), userDto.getId());
        assertThat(returnItemDto.getName(), equalTo("newItem"));
        assertThat(returnItemDto.getDescription(), equalTo("newDescription"));
        assertFalse(returnItemDto.getAvailable());
    }

    @Test
    void shouldReturnItemsByOwner() {
        UserDto ownerDto = userService.save(user);
        itemService.save(itemMapper.mapToItem(itemDto), ownerDto.getId());
        itemService.save(itemMapper.mapToItem(newItemDto), ownerDto.getId());
        List<ItemDto> listItems = itemService.findByUserId(ownerDto.getId());
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldReturnItemsByText() {
        UserDto ownerDto = userService.save(user);
        itemService.save(itemMapper.mapToItem(itemDto), ownerDto.getId());
        itemService.save(itemMapper.mapToItem(newItemDto), ownerDto.getId());
        List<ItemDto> listItems = itemService.findByText("item");
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldCreateComment() {
        UserDto ownerDto = userService.save(user);
        UserDto newUserDto = userService.save(newUser);
        ItemDto returnItemDto = itemService.save(itemMapper.mapToItem(itemDto), ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                returnItemDto.getId(),
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4));
        BookingDto bookingDto = bookingService.save(bookingInputDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), ownerDto.getId(), true);
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Comment comment = new Comment(1L, "comment", itemMapper.mapToItem(returnItemDto),
                userMapper.userDtoToUser(newUserDto), LocalDateTime.now());
        itemService.saveComment(returnItemDto.getId(), newUserDto.getId(), comment);
        Assertions.assertEquals(1, itemService.findCommentsByItemId(returnItemDto.getId()).size());
    }
}
