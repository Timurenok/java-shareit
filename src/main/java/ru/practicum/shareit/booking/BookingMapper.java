package ru.practicum.shareit.booking;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public final class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(booking.getId(),
                    booking.getItem(),
                    booking.getBooker(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getStatus());
        }
        return null;
    }

    public static BookingShortDto mapToBookingShortDto(Booking booking) {
        if (booking != null) {
            return new BookingShortDto(booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd());
        }
        return null;
    }
}