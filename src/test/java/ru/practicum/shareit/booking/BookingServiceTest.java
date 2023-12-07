package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.UnknownBookingException;
import ru.practicum.shareit.exception.UnknownUserException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private final User user = new User(null, "user", "user@gmail.com");

    private final User newUser = new User(null, "newUser", "newUser@gmail.com");

    private final ItemDto itemDto = new ItemDto(null, "item", "description", true, 1L,
            null, null, null, null);

    @Test
    void shouldThrowExceptionWhenCreateBookingByOwnerItem() {
        UserDto ownerDto = userService.save(user);
        ItemDto returnItemDto = itemService.save(ItemMapper.mapToItem(itemDto), ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(returnItemDto.getId(), LocalDateTime.now(),
                LocalDateTime.now());
        UnknownBookingException exception = assertThrows(UnknownBookingException.class,
                () -> bookingService.save(bookingInputDto, ownerDto.getId()));
        assertEquals("You can't book your item", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFindBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.save(user);
        UserDto newUserDto = userService.save(newUser);
        UserDto messiUserDto = userService.save(new User(null, "Messi", "Messi@gmail.com"));
        Long userId = messiUserDto.getId();
        ItemDto returnItemDto = itemService.save(ItemMapper.mapToItem(itemDto), ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(returnItemDto.getId(), LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4));
        BookingDto bookingDto = bookingService.save(bookingInputDto, newUserDto.getId());
        UnknownUserException exception = assertThrows(UnknownUserException.class,
                () -> bookingService.findById(userId, bookingDto.getId()));
        assertEquals("You don't have abilities to see this booking", exception.getMessage());
    }
}
