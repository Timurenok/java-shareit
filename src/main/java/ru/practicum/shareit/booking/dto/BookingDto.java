package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    private Long itemId;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(bookerId, that.bookerId) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, bookerId, start, end, status);
    }
}
