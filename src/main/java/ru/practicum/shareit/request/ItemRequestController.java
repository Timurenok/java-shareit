package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveRequest(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request {}", itemRequestDto);
        return itemRequestService.save(requesterId, itemRequestDto);
    }

    @GetMapping("{id}")
    public ItemRequestDto findRequest(@RequestHeader("X-Sharer-User-Id") Long requesterId, @PathVariable Long id) {
        log.info("Getting request with id {}", id);
        return itemRequestService.findById(requesterId, id);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequesterId(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Getting requests with requestId {}", requesterId);
        return itemRequestService.findByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findPages(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(required = false) Integer size) {
        log.info("Getting requests by pages");
        return itemRequestService.findByPages(requesterId, from, size);
    }
}
