package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.HotelMinimumPrice;
import com.project.airbnb_app.repository.HotelMinimumPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelMinimumPriceDomainService {

    private final HotelMinimumPriceRepository hotelMinimumPriceRepository;

    Optional<HotelMinimumPrice> getHotelByHotelAndDate(Hotel hotel, LocalDate localDate) {
        return hotelMinimumPriceRepository.findHotelByHotelAndDate(hotel, localDate);
    }
}
