package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;

public interface HotelBookingService {

    HotelBooking findByPaymentSessionId(String paymentSessionId);

    String getBookingStatus(Long bookingId);

    void saveBooking(HotelBooking hotelBooking);
}
