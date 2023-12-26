package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Creating request {}", itemRequestDto);
        return itemRequestClient.saveItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemRequestById(@PathVariable("requestId") Long id,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting request by id {}", id);
        return itemRequestClient.findItemRequestById(userId, id);
    }


    @GetMapping
    public ResponseEntity<Object> findOwnItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting requests by requestId {}", requesterId);
        return itemRequestClient.findOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(required = false) Integer size) {
        log.info("Getting requests by pages");
        return itemRequestClient.findAllItemRequests(userId, from, size);
    }
}