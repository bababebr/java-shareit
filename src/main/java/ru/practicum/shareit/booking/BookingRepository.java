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

    @Query("SELECT b FROM Booking as b WHERE b.booker = ?1 and b.state = ?2 ORDER BY b.start DESC ")
    List<Booking> findBookingsByBooker_IdAndState(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking as b WHERE b.owner = ?1 and b.state = ?2 ORDER BY b.start DESC ")
    List<Booking> findBookingsByOwner_IdAndState(Long ownerId, BookingStatus status);

    List<Booking> findBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByOwner_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findBookingsByOwner_IdAndEndIsBeforeOrderByStartAsc(Long userId, LocalDateTime time);

    @Query("SELECT b from Booking as b WHERE (b.booker = ?1) AND (?2 BETWEEN b.start AND b.end) ORDER BY b.start DESC ")
    List<Booking> findBookingsByBookerCurrent(Long userId, LocalDateTime time);

    @Query("SELECT b from Booking as b WHERE (b.owner = ?1) AND (?2 BETWEEN b.start AND b.end) ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerCurrent(Long userId, LocalDateTime time);
}
