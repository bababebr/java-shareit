package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByItem_Id(Long itemId);

    List<Booking> findBookingsByBooker_Id(Long userId);

    List<Booking> findBookingsByOwner_IdAndState(Long ownerId, String status);
}
