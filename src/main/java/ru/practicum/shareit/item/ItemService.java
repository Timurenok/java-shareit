package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item, long ownerId);

    Item update(long id, Item item, long ownerId);

    Item find(long id);

    List<Item> findAllByUserId(long userId);

    List<Item> findByText(String text);
}
