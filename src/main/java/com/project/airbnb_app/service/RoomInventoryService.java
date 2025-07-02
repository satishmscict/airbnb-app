package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.RoomInventory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoomInventoryService {

    void createInventory(Long hotelId, Long roomId);

    void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId);

    Page<HotelDto> searchHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);

    List<RoomInventory> updateReservedRoomsCount(HotelBookingRequest hotelBookingRequest);
}
