package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import org.springframework.data.domain.Page;

public interface HotelOrchestratorService {

    HotelDto activateHotel(Long hotelId);

    String deleteHotelWithDependencies(Long hotelId);

    Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest);
}
