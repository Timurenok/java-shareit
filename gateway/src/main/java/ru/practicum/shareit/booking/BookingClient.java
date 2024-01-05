package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> findBookingsByUserId(Long userId, BookingState state, Integer from, Integer size) {
        String path = "?state=" + state.name() + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, null);
    }

    public ResponseEntity<Object> findBookingsByOwnerId(Long userId, BookingState state, Integer from, Integer size) {
        String path = "/owner?state=" + state.name() + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, null);
    }


    public ResponseEntity<Object> saveBooking(Long userId, BookingInputDto bookingInputDto) {
        return post("", userId, bookingInputDto);
    }

    public ResponseEntity<Object> findBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBooking(Long bookingId, Long userId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, null);
    }
}