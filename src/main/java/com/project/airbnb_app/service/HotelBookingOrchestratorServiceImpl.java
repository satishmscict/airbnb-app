package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelBookingOrchestratorServiceImpl implements HotelBookingOrchestratorService {

    private final HotelBookingRepository hotelBookingRepository;

    @Override
    public void save(HotelBooking hotelBooking) {
        hotelBookingRepository.save(hotelBooking);
    }
}
