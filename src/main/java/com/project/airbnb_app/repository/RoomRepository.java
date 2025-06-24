package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    void deleteByIdAndHotelId(Long id, Long hotelId);

    Optional<Room> findByIdAndHotelId(Long roomId, Long hotelId);
}
