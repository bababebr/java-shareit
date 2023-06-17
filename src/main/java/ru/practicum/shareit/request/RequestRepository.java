package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest as ir WHERE ir.userId =?1")
    List<ItemRequest> findAllByUserId(Long userId);

}
