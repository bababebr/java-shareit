package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    @Query("select it from Item as it join it.owner as o where o.id =?1")
    List<Item> findItemsByUserId(long userId);


    @Query("select it from Item as it where it.name like concat('%',?1,'%') " +
            "or it.description like concat('%',?1,'%') ")
    List<Item> findItemByNameAndDescription(String searchText);

}