package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UnknownBookingException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository mockBookingRepository;

    private BookingService bookingService;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(mockBookingRepository, null, null, null,
                null, null);
    }

    @Test
    void shouldThrowExceptionWhenFindBookingByWrongId() {
        when(mockBookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UnknownBookingException exception = Assertions.assertThrows(
                UnknownBookingException.class,
                () -> bookingService.findById(1L, 100L));
        Assertions.assertEquals("Booking with id 100 does not exist", exception.getMessage());
    }
}
