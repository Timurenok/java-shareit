package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long userId);

    @Query("select item " +
            "from Item as item " +
            "where (lower(item.name) like lower(concat('%', :text, '%')) " +
            "or lower(item.description) like lower(concat('%', :text, '%'))) " +
            "and item.available = true")
    List<Item> findByText(String text);

    List<Item> findByRequestId(Long requestId);
}
