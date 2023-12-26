package ru.practicum.shareit.booking;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto mapToBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingShortDto mapToBookingShortDto(Booking booking);
}