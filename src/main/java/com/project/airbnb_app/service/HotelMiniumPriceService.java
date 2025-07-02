package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelMinimumPrice;

import java.util.List;

public interface HotelMiniumPriceService {

    void saveAll(List<HotelMinimumPrice> hotelMinimumPricesList);
}
