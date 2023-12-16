package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

@Component
public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                UserMapper.mapToUser(itemRequestDto.getRequester()),
                itemRequestDto.getCreated());
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.mapToUserDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                items);
    }
}
