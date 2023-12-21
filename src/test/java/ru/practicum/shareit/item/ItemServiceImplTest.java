package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UnknownItemException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void shouldExceptionWhenFindItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(null, null, null, null, null,
                mockItemRepository, null, null);
        when(mockItemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UnknownItemException exception = Assertions.assertThrows(
                UnknownItemException.class,
                () -> itemService.find(100L, 1L));
        Assertions.assertEquals("Item with id 100 does not exist", exception.getMessage());
    }
}
