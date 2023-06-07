package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select it from Item as it join it.user as o where o.id =?1")
    List<Item> findItemsByUserId(long userId);


    @Query("select it from Item as it where UPPER(it.name) like UPPER(concat('%',?1,'%')) " +
            "or UPPER (it.description) like UPPER(concat('%',?1,'%')) and it.available = true")
    List<Item> findItemByNameAndDescription(String searchText);

}