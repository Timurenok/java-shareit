package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidDescriptionException;
import ru.practicum.shareit.exception.UnknownItemRequestException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.Pagination;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new InvalidDescriptionException("Description can't be empty");
        }
        UserDto requester = userService.find(requesterId);
        itemRequestDto.setRequester(requester);
        itemRequestDto.setCreated(LocalDateTime.now());
        return ItemRequestMapper.mapToItemRequestDto(
                itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto)), null);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> findByRequesterId(Long requesterId) {
        userService.find(requesterId);
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId).stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(request,
                        itemRepository.findByRequestId(request.getId()).stream()
                                .map(item -> ItemMapper.mapToItemDto(item, null, null, null))
                                .collect(Collectors.toList())))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDto> findByPages(Long requesterId, Integer from, Integer size) {
        userService.find(requesterId);
        Pagination pagination = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pagination.getIndex(), pagination.getPageSize(),
                Sort.by("created").descending());
        Page<ItemRequest> page = itemRequestRepository.findByRequesterIdNot(requesterId, pageable);
        return page.stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(request,
                        itemRepository.findByRequestId(request.getId()).stream()
                                .map(item -> ItemMapper
                                        .mapToItemDto(item, null, null, null))
                                .collect(Collectors.toList())))
                .collect(toList());
    }

    @Override
    @Transactional
    public ItemRequestDto findById(Long requesterId, Long id) {
        userService.find(requesterId);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findById(id).orElseThrow(() ->
                        new UnknownItemRequestException(String.format("Request with id %d does not exist", id))),
                itemRepository.findByRequestId(id).stream()
                        .map(item -> ItemMapper.mapToItemDto(item, null, null, null))
                        .collect(Collectors.toList()));
    }
}
