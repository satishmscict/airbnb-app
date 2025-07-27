package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.RoomInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    /**
     * Converts a reservation into a confirmed booking by moving rooms from reserved to booked.
     */
    @Modifying
    @Query("""
            UPDATE RoomInventory ri
            SET ri.reservedRoomsCount = ri.reservedRoomsCount - :roomsCount,
                ri.bookedRoomsCount = ri.bookedRoomsCount + :roomsCount
            WHERE ri.room.id = :roomId
               AND ri.date between :startDate and :endDate
               AND (ri.totalRoomsCount - ri.bookedRoomsCount) >= :roomsCount
               AND ri.reservedRoomsCount >= :roomsCount
               AND ri.closed = false
            """)
    void confirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    void deleteAllByHotelIdAndRoomId(Long hotelId, Long roomId);

    @Query("""
            SELECT ri
            FROM RoomInventory ri
            WHERE ri.room.id = :roomId
               AND ri.date between :startDate and :endDate
               AND ri.closed = false
               AND (ri.totalRoomsCount - ri.bookedRoomsCount - ri.reservedRoomsCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<RoomInventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Query("""
            SELECT ri
            FROM RoomInventory ri
            WHERE ri.room.id = :roomId
               AND ri.date between :startDate and :endDate
               AND ri.closed = false
               AND (ri.totalRoomsCount - ri.bookedRoomsCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<RoomInventory> findAndLockInventoryForModification(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    List<RoomInventory> findByHotelAndDateBetween(
            Hotel hotel,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            SELECT DISTINCT ri.hotel
            FROM RoomInventory ri
            WHERE ri.city = :city
                AND ri.date between :startDate AND :endDate
                AND ri.closed = false
                AND (ri.totalRoomsCount - ri.bookedRoomsCount - ri.reservedRoomsCount) >= :roomsCount
            Group by ri.hotel, ri.room
            HAVING COUNT(ri.date) >= :daysCount
            """
    )
    Page<Hotel> findHotelsByCityAndAvailability(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("daysCount") Long daysCount,
            Pageable pageable
    );

    /**
     * Releases booked rooms, making them available again.
     */
    @Modifying
    @Query("""
            UPDATE RoomInventory ri
            SET ri.bookedRoomsCount = ri.bookedRoomsCount - :roomsCount
            WHERE ri.room.id = :roomId
               AND ri.date between :startDate and :endDate
               AND (ri.totalRoomsCount - ri.bookedRoomsCount) >= :roomsCount
               AND ri.closed = false
            """)
    void releaseBookedRooms(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
                UPDATE RoomInventory ri
                SET ri.reservedRoomsCount = ri.reservedRoomsCount + :numberOfRooms
                WHERE ri.room.id = :roomId
                  AND ri.date BETWEEN :startDate AND :endDate
                  AND (ri.totalRoomsCount - ri.bookedRoomsCount - ri.reservedRoomsCount) >= :numberOfRooms
                  AND ri.closed = false
            """)
    void reserveRooms(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );
}
