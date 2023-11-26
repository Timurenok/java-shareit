package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public Booking save(BookingDto bookingDto, Long bookerId) {
        Booking booking = check(bookingDto, bookerId);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    @Transactional
    public Booking update(Long id, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new InvalidBookingException(String.format("Booking with id %d does not exist", id)));
        userService.find(ownerId);
        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new UnknownUserException("You don't have abilities to change this booking");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new InvalidBookingException("You've already changed the status of this item");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking findById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new UnknownBookingException(String.format("Booking with id %d does not exist", id)));
        userService.find(userId);
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwnerId().equals(userId)) {
            return booking;
        }
        throw new UnknownUserException("You don't have abilities to see this booking");
    }

    @Override
    @Transactional
    public List<Booking> findByUserId(Long bookerId, String state) {
        userService.find(bookerId);
        LocalDateTime now = LocalDateTime.now();
        if (state.equals(BookingState.ALL.toString())) {
            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        }
        if (state.equals(BookingState.CURRENT.toString())) {
            return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(bookerId, now, now);
        }
        if (state.equals(BookingState.PAST.toString())) {
            return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now);
        }
        if (state.equals(BookingState.FUTURE.toString())) {
            return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, now);
        }
        if (state.equals(BookingState.WAITING.toString()) ||
                state.equals(BookingState.REJECTED.toString())) {
            return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.valueOf(state));
        }
        throw new InvalidStateException(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public List<Booking> findByOwnerId(Long ownerId, String state) {
        userService.find(ownerId);
        LocalDateTime now = LocalDateTime.now();
        if (state.equals(BookingState.ALL.toString())) {
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        }
        if (state.equals(BookingState.CURRENT.toString())) {
            return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now);
        }
        if (state.equals(BookingState.PAST.toString())) {
            return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now);
        }
        if (state.equals(BookingState.FUTURE.toString())) {
            return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, now);
        }
        if (state.equals(BookingState.WAITING.toString()) ||
                state.equals(BookingState.REJECTED.toString())) {
            return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state));
        }
        throw new InvalidStateException(String.format("Unknown state: %s", state));
    }

    @Override
    public BookingDto findLastBooking(Long itemId) {
        return BookingMapper.mapToBookingDto(
                bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
    }

    @Override
    public BookingDto findNextBooking(Long itemId) {
        return BookingMapper.mapToBookingDto(bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStartAsc(itemId,
                LocalDateTime.now()));
    }

    @Override
    public Booking findBookingWithUserBookedItem(Long itemId, Long userId) {
        return bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId, userId,
                LocalDateTime.now(), BookingStatus.APPROVED);
    }

    private Booking check(BookingDto bookingDto, Long bookerId) {
        User booker = userService.find(bookerId);
        ItemDto itemDto = itemService.find(bookingDto.getItemId(), bookerId);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (itemDto.getOwnerId().equals(bookerId)) {
            throw new UnknownBookingException("You can't book your item");
        }
        if (!itemDto.getAvailable()) {
            throw new InvalidAvailableException("You can't book this item because it has already booked");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InvalidTimeException("Time can't be empty");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new InvalidTimeException("Start can't be similar to end");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeException("Booking can't start in the past");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeException("Booking can't end in the past");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new InvalidTimeException("Booking can't finish before the beginning");
        }
        return BookingMapper.mapToBooking(bookingDto, itemDto, booker);
    }
}
