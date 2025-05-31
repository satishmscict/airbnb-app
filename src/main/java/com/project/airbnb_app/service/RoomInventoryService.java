package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import org.springframework.data.domain.Page;

public interface RoomInventoryService {

    void createInventory(Long hotelId, Long roomId);

    void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId);

    Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);
}
