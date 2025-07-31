package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;

public interface HotelBookingService {

    String getBookingStatusByBookingId(Long bookingId);

    void saveBooking(HotelBooking hotelBooking);
}
