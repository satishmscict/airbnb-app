package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelAndRoomsDto;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.entity.Hotel;

import java.util.List;

public interface HotelService {

    HotelDto createHotel(HotelDto hotelDto);

    List<HotelDto> getAllHotels();

    HotelAndRoomsDto getHotelAndRoomsDetails(Long hotelId);

    HotelDto getHotelByIdAndIsActive(Long hotelId);

    void deleteHotel(Long hotelId);

    void save(Hotel hotel);
}
