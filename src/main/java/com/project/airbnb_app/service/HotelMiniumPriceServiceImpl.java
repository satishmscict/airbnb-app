package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelMinimumPrice;
import com.project.airbnb_app.repository.HotelMinimumPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelMiniumPriceServiceImpl implements HotelMiniumPriceService {

    private final HotelMinimumPriceRepository hotelMinimumPriceRepository;

    @Override
    public void saveAll(List<HotelMinimumPrice> hotelMinimumPricesList) {
        hotelMinimumPriceRepository.saveAll(hotelMinimumPricesList);
    }
}
