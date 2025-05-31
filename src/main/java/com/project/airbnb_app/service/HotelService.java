package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelInfoDto;
import com.project.airbnb_app.dto.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HotelService {

    HotelDto activateHotel(Long hotelId);

    HotelDto createHotel(HotelDto hotelDto);

    String deleteHotelById(Long hotelId);

    Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);

    List<HotelDto> getAllHotels();

    Hotel getHotelById(Long hotelId);

    HotelInfoDto getHotelDetailsInfo(Long hotelId);

    HotelDto getHotelDtoById(Long hotelId);

    Boolean isHotelExistById(Long hotelId);
}
