package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.entity.Hotel;

import java.util.List;

public interface HotelService {

    HotelDto activateHotel(Long hotelId);

    HotelDto createHotel(HotelDto hotelDto);

    String deleteHotelById(Long hotelId);

    List<HotelDto> getAllHotels();

    Hotel getHotelById(Long hotelId);

    HotelDto getHotelDtoById(Long hotelId);

    Boolean isHotelExistById(Long hotelId);
}
