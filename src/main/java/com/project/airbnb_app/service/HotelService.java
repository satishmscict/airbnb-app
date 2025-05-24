package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;

import java.util.List;

public interface HotelService {

    HotelDto activateHotel(Long hotelId);

    HotelDto createHotel(HotelDto hotelDto);

    void deleteHotelById(Long hotelId);

    List<HotelDto> getAllHotels();

    HotelDto getHotelById(Long hotelId);
}
