package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.dto.request.HotelMiniumPriceRequest;
import org.springframework.data.domain.Page;

public interface HotelMinimPriceService {

    Page<HotelMinimumPriceDto> searchHotelsByCityWithMinimumPrice(HotelMiniumPriceRequest hotelMiniumPriceRequest);
}
