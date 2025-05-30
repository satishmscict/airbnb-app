package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelInfoDto;
import com.project.airbnb_app.dto.HotelSearchRequest;
import com.project.airbnb_app.dto.RoomInventoryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoomInventoryService {

    List<RoomInventoryDto> createInventory(Long hotelId, Long roomId);

    void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId);

    HotelInfoDto getHotelDetailsInfo(Long hotelId);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
