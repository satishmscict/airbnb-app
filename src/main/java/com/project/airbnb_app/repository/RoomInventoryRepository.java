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
            SELECT DISTINCT i.hotel
            FROM RoomInventory i
            WHERE
                i.city = :city
                AND i.date between :startDate AND :endDate
                AND (i.totalRoomsCount - i.bookedRoomsCount) >= :roomsCount
                AND i.closed = false
            Group by i.hotel, i.room
            HAVING COUNT(i.date) >= :daysCount
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
