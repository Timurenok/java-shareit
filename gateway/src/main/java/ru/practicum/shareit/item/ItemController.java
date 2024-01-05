package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemClient.saveItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long id) {
        log.info("Getting item with id {}", id);
        return itemClient.findItemById(userId, id);
    }

    @ResponseBody
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating item {}", itemDto);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(required = false) Integer size) {
        log.info("Getting items by user with id {}", ownerId);
        return itemClient.findItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestParam String text,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(required = false) Integer size) {
        log.info("Getting items by text {}", text);
        return itemClient.findItemsByText(text, from, size);
    }

    @ResponseBody
    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> saveComment(@RequestBody CommentDto commentDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long id) {
        log.info("Creating comment {}", commentDto);
        return itemClient.saveComment(commentDto, id, userId);
    }
}