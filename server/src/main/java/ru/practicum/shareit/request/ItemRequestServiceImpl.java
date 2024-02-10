package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidDescriptionException;
import ru.practicum.shareit.exception.UnknownItemRequestException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new InvalidDescriptionException("Description can't be empty");
        }
        UserDto requester = userService.find(requesterId);
        itemRequestDto.setRequester(requester);
        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestMapper.itemRequestToItemRequestDto(
                itemRequestRepository.save(itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto)), null);
    }

    @Override
    @Transactional
    public ItemRequestDto findById(Long userId, Long id) {
        userService.find(userId);
        return itemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.findById(id).orElseThrow(() ->
                        new UnknownItemRequestException(String.format("Request with id %d does not exist", id))),
                itemRepository.findByRequestId(id).stream()
                        .map(item -> itemMapper.mapToItemDto(item, null, null, null))
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public List<ItemRequestDto> findByRequesterId(Long requesterId) {
        userService.find(requesterId);
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId).stream()
                .map(request -> itemRequestMapper.itemRequestToItemRequestDto(request,
                        itemRepository.findByRequestId(request.getId()).stream()
                                .map(item -> itemMapper.mapToItemDto(item, null, null, null))
                                .collect(Collectors.toList())))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDto> findByPages(Long requesterId, Pageable pageable) {
        userService.find(requesterId);
        Page<ItemRequest> page = itemRequestRepository.findByRequesterIdNot(requesterId, pageable);
        return page.stream()
                .map(request -> itemRequestMapper.itemRequestToItemRequestDto(request,
                        itemRepository.findByRequestId(request.getId()).stream()
                                .map(item -> itemMapper
                                        .mapToItemDto(item, null, null, null))
                                .collect(Collectors.toList())))
                .collect(toList());
    }
}
