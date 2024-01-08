package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Creating item {}", item);
        return itemService.save(item, ownerId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@PathVariable Long id,
                              @RequestBody Item item,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Updating item {}", item);
        return itemService.update(id, item, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto findItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item with id {}", id);
        return itemService.find(id, userId);
    }

    @GetMapping
    public List<ItemDto> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting items by user with id {}", userId);
        return itemService.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestParam String text) {
        log.info("Getting items by text {}", text);
        return itemService.findByText(text);
    }

    @PostMapping("/{id}/comment")
    public CommentDto saveComment(@PathVariable Long id,
                                  @RequestHeader("X-Sharer-User-Id") Long authorId,
                                  @RequestBody Comment comment) {
        log.info("Creating comment {}", comment);
        return itemService.saveComment(id, authorId, comment);
    }
}
