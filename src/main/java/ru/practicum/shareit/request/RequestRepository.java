package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterIdIsNot(Long userId, Pageable pageable);

}
