package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;

public interface HotelBookingService {

    HotelBookingDto crateHotelBooking(HotelBookingRequest hotelBookingRequest);
}
