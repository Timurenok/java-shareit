package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Creating request {}", itemRequestDto);
        return itemRequestClient.saveItemRequest(itemRequestDto, requesterId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long id) {
        log.info("Getting request by id {}", id);
        return itemRequestClient.findItemRequestById(userId, id);
    }


    @GetMapping
    public ResponseEntity<Object> findItemRequestsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Getting requests by requestId {}", ownerId);
        return itemRequestClient.findOwnItemRequests(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(required = false) Integer size) {
        log.info("Getting requests by pages");
        return itemRequestClient.findAllItemRequests(userId, from, size);
    }
}