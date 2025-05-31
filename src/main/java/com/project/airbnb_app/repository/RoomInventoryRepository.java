package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.RoomInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    void deleteAllByHotelIdAndRoomId(Long hotelId, Long roomId);

    @Query("""
            SELECT DISTINCT ri.hotel
            FROM RoomInventory ri
            WHERE
                ri.city = :city
                AND ri.date between :startDate AND :endDate
                AND (ri.totalRoomsCount - ri.bookedRoomsCount) >= :roomsCount
                AND ri.closed = false
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
}
