package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.dto.request.HotelMiniumPriceRequest;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import org.springframework.data.domain.Page;

public interface HotelOrchestratorService {

    HotelDto activateHotel(Long hotelId);

    String deleteHotelWithDependencies(Long hotelId);

    Page<HotelDto> searchHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);

    Page<HotelMinimumPriceDto> searchHotelsByCityWithMiniumPrice(HotelMiniumPriceRequest hotelMiniumPriceRequest);
}
