package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UnknownItemRequestException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    private ItemRequestService itemRequestService;

    @Mock
    private final UserService mockUserService;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserService, null,
                null, null);
    }

    @Test
    void shouldThrowExceptionWhenGetItemRequestWithWrongId() {
        when(mockUserService.find(any()))
                .thenReturn(new UserDto(1L, "user", "user@gmail.com"));
        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UnknownItemRequestException exception = Assertions.assertThrows(
                UnknownItemRequestException.class,
                () -> itemRequestService.findById(1L, 100L));
        Assertions.assertEquals("Request with id 100 does not exist", exception.getMessage());
    }
}
