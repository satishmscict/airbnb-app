package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.HotelBookingRequest;

public interface HotelBookingService {

    HotelBookingDto crateBooking(HotelBookingRequest hotelBookingRequest);
}
