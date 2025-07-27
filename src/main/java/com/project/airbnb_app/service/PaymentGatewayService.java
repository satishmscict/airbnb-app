package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.stripe.model.Event;

public interface PaymentGatewayService {

    void capturePaymentEvent(Event event);

    String createCheckoutSession(HotelBooking hotelBooking);

    void processRefundAmount(HotelBooking hotelBooking);
}
