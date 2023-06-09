package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByItem_Id(Long itemId);

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findBookingsByOwner_IdOrderByStartDesc(Long userId);

    List<Booking> findBookingsByBooker_IdAndStateOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findBookingsByOwner_IdAndStateOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByOwner_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByOwner_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId, LocalDateTime end, LocalDateTime start);

    List<Booking> findBookingsByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId, LocalDateTime end, LocalDateTime start);

    @Query("SELECT b from Booking as b JOIN Item it on b.item.id = it.id WHERE it.id = ?1 order by b.start desc")
    List<Booking> findItemsBooking(Long itemId);

    List<Booking> findBookingByBooker_idAndItem_Id(Long bookerId, Long itemId);
}
