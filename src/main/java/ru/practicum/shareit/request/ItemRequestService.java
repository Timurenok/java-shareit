package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findByRequesterId(Long requesterId);

    List<ItemRequestDto> findByPages(Long requesterId, Integer from, Integer size);

    ItemRequestDto findById(Long requesterId, Long id);
}
