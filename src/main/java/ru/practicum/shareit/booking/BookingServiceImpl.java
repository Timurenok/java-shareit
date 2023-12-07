package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.Pagination;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto save(BookingInputDto bookingInputDto, Long bookerId) {
        Booking booking = check(bookingInputDto, bookerId);
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long id, Long ownerId, Boolean approved) {
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
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto findById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new UnknownBookingException(String.format("Booking with id %d does not exist", id)));
        userService.find(userId);
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.mapToBookingDto(booking);
        }
        throw new UnknownUserException("You don't have abilities to see this booking");
    }

    @Override
    @Transactional
    public List<BookingDto> findByUserId(Long bookerId, String state, Integer from, Integer size) {
        userService.find(bookerId);
        List<BookingDto> listBookingDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);
        if (size == null) {
            pageable = PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
            do {
                page = findByUserIdByPages(bookerId, state, pageable);
                listBookingDto.addAll(page.stream().map(BookingMapper::mapToBookingDto).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());
        } else {
            for (int i = pager.getIndex(); i < pager.getAmountOfPages(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = findByUserIdByPages(bookerId, state, pageable);
                listBookingDto.addAll(page.stream().map(BookingMapper::mapToBookingDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listBookingDto = listBookingDto.stream().limit(size).collect(toList());
        }
        return listBookingDto;
    }

    @Override
    @Transactional
    public List<BookingDto> findByOwnerId(Long ownerId, String state, Integer from, Integer size) {
        userService.find(ownerId);
        List<BookingDto> listBookingDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);
        if (size == null) {
            pageable = PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
            do {
                page = findByOwnerIdByPages(ownerId, state, pageable);
                listBookingDto.addAll(page.stream().map(BookingMapper::mapToBookingDto).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());
        } else {
            for (int i = pager.getIndex(); i < pager.getAmountOfPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = findByOwnerIdByPages(ownerId, state, pageable);
                listBookingDto.addAll(page.stream().map(BookingMapper::mapToBookingDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listBookingDto = listBookingDto.stream().limit(size).collect(toList());
        }
        return listBookingDto;
    }

    @Override
    public BookingShortDto findLastBooking(Long itemId) {
        return BookingMapper.mapToBookingShortDto(
                bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
    }

    @Override
    public BookingShortDto findNextBooking(Long itemId) {
        return BookingMapper.mapToBookingShortDto(bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusInOrderByStartAsc(
                itemId,
                LocalDateTime.now(), List.of(BookingStatus.WAITING, BookingStatus.APPROVED)));
    }

    @Override
    public BookingDto findBookingWithUserBookedItem(Long itemId, Long userId) {
        return BookingMapper.mapToBookingDto(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                itemId,
                userId,
                LocalDateTime.now(), BookingStatus.APPROVED));
    }

    private Booking check(BookingInputDto bookingInputDto, Long bookerId) {
        Booking booking = new Booking();
        User booker = UserMapper.mapToUser(userService.find(bookerId));
        ItemDto itemDto = itemService.find(bookingInputDto.getItemId(), bookerId);
        booking.setStart(bookingInputDto.getStart());
        booking.setEnd(bookingInputDto.getEnd());
        booking.setItem(ItemMapper.mapToItem(itemDto));
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        if (itemDto.getOwnerId().equals(bookerId)) {
            throw new UnknownBookingException("You can't book your item");
        }
        if (!itemDto.getAvailable()) {
            throw new InvalidAvailableException("You can't book this item because it has already booked");
        }
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new InvalidTimeException("Time can't be empty");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new InvalidTimeException("Start can't be similar to end");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeException("Booking can't start in the past");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeException("Booking can't end in the past");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new InvalidTimeException("Booking can't finish before the beginning");
        }
        return booking;
    }

    private Page<Booking> findByUserIdByPages(Long bookerId, String state, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        if (state.equals(BookingState.ALL.toString())) {
            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
        }
        if (state.equals(BookingState.CURRENT.toString())) {
            return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                    bookerId, now, now, pageable);
        }
        if (state.equals(BookingState.PAST.toString())) {
            return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now, pageable);
        }
        if (state.equals(BookingState.FUTURE.toString())) {
            return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, now, pageable);
        }
        if (state.equals(BookingState.WAITING.toString()) ||
                state.equals(BookingState.REJECTED.toString())) {
            return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.valueOf(state), pageable);
        }
        throw new InvalidStateException(String.format("Unknown state: %s", state));
    }

    private Page<Booking> findByOwnerIdByPages(Long ownerId, String state, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        if (state.equals(BookingState.ALL.toString())) {
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        }
        if (state.equals(BookingState.CURRENT.toString())) {
            return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                    ownerId, now, now, pageable);
        }
        if (state.equals(BookingState.PAST.toString())) {
            return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now, pageable);
        }
        if (state.equals(BookingState.FUTURE.toString())) {
            return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, now, pageable);
        }
        if (state.equals(BookingState.WAITING.toString()) ||
                state.equals(BookingState.REJECTED.toString())) {
            return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state), pageable);
        }
        throw new InvalidStateException(String.format("Unknown state: %s", state));
    }
}
