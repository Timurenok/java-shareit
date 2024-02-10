package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody BookingInputDto bookingInputDto) {
        log.info("Creating booking {}", bookingInputDto);
        return bookingClient.saveBooking(userId, bookingInputDto);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all")
                                                       String state,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(required = false) Integer size) {
        log.info("Getting bookings of booker with id {}", userId);
        return bookingClient.findBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByOwnerId(@RequestParam(name = "state", defaultValue = "all")
                                                        String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(required = false) Integer size) {
        log.info("Getting bookings of owner with id {}", userId);
        return bookingClient.findBookingsByOwnerId(userId, state, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long id) {
        log.info("Getting booking with id {}", id);
        return bookingClient.findBookingById(userId, id);
    }

    @ResponseBody
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long id,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam Boolean approved) {
        log.info("Updating booking with id {}", id);
        return bookingClient.updateBooking(id, userId, approved);
    }
}