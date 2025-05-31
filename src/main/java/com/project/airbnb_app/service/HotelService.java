package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelAndRoomsDto;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HotelService {

    HotelDto activateHotel(Long hotelId);

    HotelDto createHotel(HotelDto hotelDto);

    String deleteHotelById(Long hotelId);

    Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);

    List<HotelDto> getAllHotels();

    Hotel getHotelByIdAndIsActive(Long hotelId, Boolean isActive);

    HotelAndRoomsDto getHotelAndRoomsDetails(Long hotelId);

    HotelDto getHotelDtoByIdAndIsActive(Long hotelId);

    Boolean isHotelExistById(Long hotelId);
}
