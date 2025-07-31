package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.RoomInventoryDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.dto.request.RoomInventoryRequest;
import com.project.airbnb_app.entity.RoomInventory;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface RoomInventoryService {

    void createInventory(Long hotelId, Long roomId);

    void decreaseBookedRoomsCount(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount);

    void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId);

    void findAndLockInventoryForModification(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount);

    List<RoomInventoryDto> getRoomInventoryByRoomId(Long roomId);

    void saveAll(List<RoomInventory> roomInventories);

    Page<HotelDto> searchHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);

    void updateBookedRoomsCount(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount);

    void updateRoomInventory(Long roomId, RoomInventoryRequest roomInventoryRequest);
}
