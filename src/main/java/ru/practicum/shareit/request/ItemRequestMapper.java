package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class ItemRequestMapper {

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "requester", expression = "java(userMapper.userDtoToUser(itemRequestDto.getRequester()))")
    public abstract ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requester", expression = "java(userMapper.userToUserDto(itemRequest.getRequester()))")
    public abstract ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest, List<ItemDto> items);
}
