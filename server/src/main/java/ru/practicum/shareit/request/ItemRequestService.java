package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findByRequesterId(Long requesterId);

    List<ItemRequestDto> findByPages(Long requesterId, Pageable pageable);

    ItemRequestDto findById(Long requesterId, Long id);
}
