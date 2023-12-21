package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.request.model.Pagination;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto saveBooking(@RequestBody BookingInputDto bookingInputDto,
                                  @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Creating booking {}", bookingInputDto);
        return bookingService.save(bookingInputDto, bookerId);
    }

    @PatchMapping("/{id}")
    public BookingDto updateBooking(@PathVariable Long id,
                                    @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @RequestParam Boolean approved) {
        log.info("Updating booking with id {}", id);
        return bookingService.update(id, ownerId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long id) {
        log.info("Getting booking with id {}", id);
        return bookingService.findById(userId, id);
    }

    @GetMapping
    public List<BookingDto> findByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                         String state,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(required = false) Integer size) {
        log.info("Getting bookings of booker with id {}", userId);
        Pagination pagination = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pagination.getIndex(), pagination.getPageSize(),
                Sort.by("start").descending());
        return bookingService.findByUserId(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                          String state,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(required = false) Integer size) {
        log.info("Getting bookings of owner with id {}", userId);
        Pagination pagination = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pagination.getIndex(), pagination.getPageSize(),
                Sort.by("start").descending());
        return bookingService.findByOwnerId(userId, state, pageable);
    }
}
