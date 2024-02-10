package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {

    BookingDto save(BookingInputDto bookingInputDto, Long bookerId);

    BookingDto update(Long id, Long ownerId, Boolean approved);

    BookingDto findById(Long userId, Long id);

    List<BookingDto> findByUserId(Long bookerId, String state, Pageable pageable);

    List<BookingDto> findByOwnerId(Long ownerId, String state, Pageable pageable);

    BookingShortDto findLastBooking(Long itemId);

    BookingShortDto findNextBooking(Long itemId);

    BookingDto findBookingWithUserBookedItem(Long itemId, Long userId);
}
