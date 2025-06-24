package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;

import java.util.List;

public interface HotelBookingService {

    List<GuestDto> addGuestsToBooking(Long bookingId, List<GuestDto> guestList);

    HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest);
}
