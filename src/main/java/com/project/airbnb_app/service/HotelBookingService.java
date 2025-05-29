package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.BookingDto;
import com.project.airbnb_app.dto.HotelBookingRequest;

public interface HotelBookingService {

    BookingDto crateBooking(HotelBookingRequest hotelBookingRequest);
}
