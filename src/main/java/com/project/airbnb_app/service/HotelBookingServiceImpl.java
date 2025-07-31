package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingServiceImpl implements HotelBookingService {

    private final HotelBookingDomainService hotelBookingDomainService;
    private final HotelBookingRepository hotelBookingRepository;

    @Override
    public String getBookingStatusByBookingId(Long bookingId) {
        return hotelBookingDomainService.getBookingStatusByBookingId(bookingId);
    }

    @Override
    public void saveBooking(HotelBooking hotelBooking) {
        hotelBookingRepository.save(hotelBooking);
    }
}
