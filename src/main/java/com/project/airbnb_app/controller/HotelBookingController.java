package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.HotelBookingRequest;
import com.project.airbnb_app.service.HotelBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotelBookings")
@Tag(name = "Hotel Booking API")
public class HotelBookingController {

    private final HotelBookingService hotelBookingService;

    @PostMapping
    public ResponseEntity<HotelBookingDto> createBooking(@Valid @RequestBody HotelBookingRequest hotelBookingRequest) {
        HotelBookingDto hotelBookingDto = hotelBookingService.crateBooking(hotelBookingRequest);
        return new ResponseEntity<>(hotelBookingDto, HttpStatus.CREATED);
    }
}
