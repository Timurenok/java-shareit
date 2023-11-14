package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private static long id = 1;

    @Override
    public Item save(Item item, long ownerId) {
        if (userService.find(ownerId) == null) {
            throw new UnknownUserException(String.format("User with id %d does not exist", id));
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new InvalidNameException("Name can't be empty");
        }
        if (item.getDescription() == null) {
            throw new InvalidDescriptionException("Description can't be empty");
        }
        if (item.getAvailable() == null) {
            throw new InvalidAvailableException("Available can't be empty");
        }
        item.setId(generateId());
        item.setOwner(userService.find(ownerId));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long id, Item item, long ownerId) {
        find(id);
        if (items.get(id).getOwner().getId() == ownerId) {
            item.setId(id);
            item.setOwner(items.get(id).getOwner());
            if (item.getName() == null) {
                item.setName(items.get(id).getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(items.get(id).getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(items.get(id).getAvailable());
            }
            items.put(id, item);
            return item;
        }
        throw new UnknownUserException("You don't have an ability to change this item");
    }

    @Override
    public Item find(long id) {
        return Optional.ofNullable(items.get(id)).orElseThrow(() -> new UnknownItemException(
                String.format("Item with id %d does not exist", id)));
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream().filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                && item.getAvailable()).collect(Collectors.toList());
    }

    private long generateId() {
        return id++;
    }
}
