package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto save(Item item, Long ownerId);

    ItemDto update(Long id, Item item, Long ownerId);

    ItemDto find(Long id, Long userId);

    List<ItemDto> findByUserId(Long userId);

    List<ItemDto> findByText(String text);

    List<ItemDto> findByRequestId(Long requestId);

    CommentDto saveComment(Long id, Long authorId, Comment comment);

    List<CommentDto> findCommentsByItemId(Long itemId);

    List<CommentDto> findCommentsByAuthorId(Long authorId);
}
