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

    List<Booking> findBookingsByOwner_IdAndEndIsBeforeOrderByStartAsc(Long userId, LocalDateTime time);

    @Query("SELECT b from Booking as b WHERE (b.booker = ?1) AND (?2 BETWEEN b.start AND b.end) ORDER BY b.start DESC ")
    List<Booking> findBookingsByBookerCurrent(Long userId, LocalDateTime time);

    @Query("SELECT b from Booking as b WHERE (b.owner = ?1) AND (?2 BETWEEN b.start AND b.end) ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerCurrent(Long userId, LocalDateTime time);

    @Query("SELECT b from Booking as b JOIN Item it on b.item.id = it.id WHERE it.id = ?1 order by b.start desc")
    List<Booking> findItemsBooking(Long itemId);

    Booking findBookingByBooker_idAndItem_Id(Long bookerId, Long itemId);
}
