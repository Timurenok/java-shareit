package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.Pagination;

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
        log.info("Getting request by id {}", id);
        return itemRequestService.findById(requesterId, id);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequesterId(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Getting requests by requestId {}", requesterId);
        return itemRequestService.findByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findPages(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(required = false) Integer size) {
        log.info("Getting requests by pages");
        Pagination pagination = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pagination.getIndex(), pagination.getPageSize(),
                Sort.by("created").descending());
        return itemRequestService.findByPages(requesterId, pageable);
    }
}
