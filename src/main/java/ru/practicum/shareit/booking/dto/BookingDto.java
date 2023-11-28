package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    private Item item;

    private User booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return Objects.equals(item, that.item) &&
                Objects.equals(booker, that.booker) &&
                Objects.equals(start, that.start) && Objects.equals(end, that.end) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, booker, start, end, status);
    }
}
