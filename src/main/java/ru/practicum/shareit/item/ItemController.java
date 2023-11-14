package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item saveItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Creating item {}", item);
        return itemService.save(item, ownerId);
    }

    @PatchMapping("{id}")
    public Item updateItem(@PathVariable long id,
                           @RequestBody Item item,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Updating item {}", item);
        return itemService.update(id, item, ownerId);
    }

    @GetMapping("{id}")
    public Item findItem(@PathVariable long id) {
        log.info("Getting item with id {}", id);
        return itemService.find(id);
    }

    @GetMapping
    public List<Item> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting items by user with id {}", userId);
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> findItemByText(@RequestParam String text) {
        log.info("Getting items by text {}", text);
        return itemService.findByText(text);
    }
}
