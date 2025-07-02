package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.dto.request.HotelMiniumPriceRequest;
import com.project.airbnb_app.repository.HotelMinimumPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelMinimPriceServiceImpl implements HotelMinimPriceService {

    private final HotelMinimumPriceRepository hotelMinimumPriceRepository;

    @Override
    public Page<HotelMinimumPriceDto> searchHotelsByCityWithMinimumPrice(HotelMiniumPriceRequest hotelMiniumPriceRequest) {
        log.debug("Find hotels with minimums price by city: {}, start date: {} and end date: {}.",
                hotelMiniumPriceRequest.getCity(),
                hotelMiniumPriceRequest.getStartDate(),
                hotelMiniumPriceRequest.getEndDate()
        );
        Pageable pageable = PageRequest.of(hotelMiniumPriceRequest.getPageNo(), hotelMiniumPriceRequest.getPageSize());

        Page<HotelMinimumPriceDto> hotelsList = hotelMinimumPriceRepository.findHotelsByCityWithCheapestPrice(
                hotelMiniumPriceRequest.getCity(),
                hotelMiniumPriceRequest.getStartDate(),
                hotelMiniumPriceRequest.getEndDate(),
                pageable
        );
        log.debug("Total {} hotel min prices found.", hotelsList.getContent().size());

        return hotelsList;
    }
}
