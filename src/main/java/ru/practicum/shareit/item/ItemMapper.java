package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "item.id")
    ItemDto mapToItemDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking,
                         List<CommentDto> comments);

    Item mapToItem(ItemDto itemDto);
}