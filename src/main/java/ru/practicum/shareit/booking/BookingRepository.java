package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItem_IdOrderByStartDesc(Long itemId);

    List<Booking> findByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findByOwner_IdOrderByStartDesc(Long userId);

    List<Booking> findByBooker_IdAndState(Long userId, BookingStatus status);

    List<Booking> findByOwner_IdAndState(Long ownerId, BookingStatus status);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByOwner_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByOwner_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBooker_IdAndEndIsAfterAndStartIsBefore(Long userId, LocalDateTime end, LocalDateTime start);

    List<Booking> findByOwner_IdAndEndIsAfterAndStartIsBefore(Long userId, LocalDateTime end, LocalDateTime start);

    Booking findByOwnerAndId(User user, Long id);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.state = ?3 order by b.start desc")
    List<Booking> findByBookerAndItem(Long bookerId, Long itemId, BookingStatus state);
}
