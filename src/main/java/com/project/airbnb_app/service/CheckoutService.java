package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;

public interface CheckoutService {

    String createCheckoutSession(HotelBooking hotelBooking);
}
