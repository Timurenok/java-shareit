package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item, Long ownerId);

    Item update(Long id, Item item, Long ownerId);

    ItemDto find(Long id, Long userId);

    List<ItemDto> findByUserId(Long userId);

    List<Item> findByText(String text);

    CommentDto saveComment(Long id, Long authorId, Comment comment);

    List<CommentDto> findCommentsByItemId(Long itemId);

    List<CommentDto> findCommentsByAuthorId(Long authorId);
}
