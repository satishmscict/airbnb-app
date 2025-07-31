package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.HotelBookingReportDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;

import java.time.LocalDate;
import java.util.List;

public interface HotelBookingOrchestratorService {

    List<GuestDto> assignGuestsToBooking(Long bookingId, List<Long> guestIds);

    void cancelBooking(Long bookingId);

    HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest);

    List<HotelBookingDto> getAllBookingsByHotelId(Long hotelId);

    List<HotelBookingDto> getAllBookingsByUserId();

    HotelBookingReportDto getBookingReportByHotelIdAndDateRange(Long hotelId, LocalDate startDate, LocalDate endDate);

    String initiatePayment(Long bookingId);
}
