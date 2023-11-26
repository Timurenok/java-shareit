package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking save(BookingDto bookingDto, Long bookerId);

    Booking update(Long id, Long ownerId, Boolean approved);

    Booking findById(Long userId, Long id);

    List<Booking> findByUserId(Long bookerId, String state);

    List<Booking> findByOwnerId(Long ownerId, String state);

    BookingDto findLastBooking(Long itemId);

    BookingDto findNextBooking(Long itemId);

    Booking findBookingWithUserBookedItem(Long itemId, Long userId);
}
