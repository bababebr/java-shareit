package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item as it join it.owner as o where o.id =?1")
    List<Item> findItemsByOwner(long ownerId);

    @Query("select it from Item as it where UPPER(it.name) like UPPER(concat('%',?1,'%')) " +
            "or UPPER (it.description) like UPPER(concat('%',?1,'%')) and it.available = true")
    List<Item> findItemByNameAndDescription(String searchText);
}