package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteAllByDateIsAfterAndHotelIdAndRoomId(LocalDate currentDate, Long hotelId, Long roomId);

    void deleteAllByHotelIdAndRoomId(Long hotelId, Long roomId);
}
