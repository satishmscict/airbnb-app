package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.HotelBookingReportResponseDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;

import java.time.LocalDate;
import java.util.List;

public interface HotelBookingOrchestratorService {

    List<GuestDto> addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList);

    void cancelBooking(Long bookingId);

    HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest);

    List<HotelBookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelBookingReportResponseDto getBookingReportByHotelIdAndDateRange(Long hotelId, LocalDate startDate, LocalDate endDate);

    String initiatePayment(Long bookingId);
}
