package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto save(Item item, Long ownerId) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new InvalidNameException("Name can't be empty");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new InvalidDescriptionException("Description can't be empty");
        }
        if (item.getAvailable() == null) {
            throw new InvalidAvailableException("Available can't be empty");
        }
        if (userService.find(ownerId) == null) {
            throw new UnknownUserException(String.format("User with id %d does not exist", ownerId));
        }
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item, null, null, null);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, Item item, Long ownerId) {
        if (itemRepository.findById(id).isPresent()) {
            if (userService.find(itemRepository.findById(id).get().getOwnerId()).getId().equals(ownerId)) {
                item.setId(id);
                item.setOwnerId(ownerId);
                if (item.getName() == null) {
                    item.setName(itemRepository.findById(id).get().getName());
                }
                if (item.getDescription() == null) {
                    item.setDescription(itemRepository.findById(id).get().getDescription());
                }
                if (item.getAvailable() == null) {
                    item.setAvailable(itemRepository.findById(id).get().getAvailable());
                }
                itemRepository.save(item);
                return ItemMapper.mapToItemDto(item, null, null, null);
            }
            throw new UnknownUserException("You don't have an ability to change this item");
        }
        throw new UnknownItemException(String.format("Item with id %d does not exist", id));
    }

    @Override
    @Transactional
    public ItemDto find(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new UnknownItemException(
                String.format("Item with id %d does not exist", id)));
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByEndDesc(id,
                LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusInOrderByStartAsc(id,
                LocalDateTime.now(), List.of(BookingStatus.WAITING, BookingStatus.APPROVED));
        if (!item.getOwnerId().equals(userId)) {
            return ItemMapper.mapToItemDto(item, null, null, findCommentsByItemId(id));
        }
        return ItemMapper.mapToItemDto(item,
                BookingMapper.mapToBookingShortDto(lastBooking),
                BookingMapper.mapToBookingShortDto(nextBooking),
                findCommentsByItemId(id));
    }

    @Override
    @Transactional
    public List<ItemDto> findByUserId(Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> find(item.getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream()
                .map(item -> ItemMapper.mapToItemDto(item, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long id, Long authorId, Comment comment) {
        Item item = ItemMapper.mapToItem(find(id, authorId));
        User author = userService.find(authorId);
        if (comment.getText().isBlank()) {
            throw new InvalidCommentException("Comment can't be empty");
        }
        if (bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(id, authorId, LocalDateTime.now(),
                BookingStatus.APPROVED) == null) {
            throw new InvalidCommentException("You can't make a comment, because you haven't booked this item");
        }
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public List<CommentDto> findCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentDto> findCommentsByAuthorId(Long authorId) {
        return commentRepository.findByAuthorId(authorId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}
