package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest());
    }

}
