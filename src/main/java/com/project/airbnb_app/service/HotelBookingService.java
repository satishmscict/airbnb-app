package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.stripe.model.Event;

import java.util.List;

public interface HotelBookingService {

    List<GuestDto> addGuestsToBooking(Long bookingId, List<GuestDto> guestList);

    void cancelBooking(Long bookingId);

    void capturePaymentEvent(Event event);

    HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest);

    String getBookingStatus(Long bookingId);

    String initiatePayment(Long bookingId);
}
