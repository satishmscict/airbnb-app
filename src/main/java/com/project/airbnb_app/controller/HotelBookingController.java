package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.GuestCreateDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
@Tag(name = "Hotel Booking API")
public class HotelBookingController {

    private final HotelBookingService hotelBookingService;

    @PostMapping("/guests")
    ResponseEntity<List<GuestDto>> addGuestsToBooking(@RequestBody GuestCreateDto guestCreate) {
        return new ResponseEntity<>(
                hotelBookingService.addGuestsToBooking(
                        guestCreate.getBookingId(),
                        guestCreate.getGuest()
                ), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<HotelBookingDto> createBooking(@Valid @RequestBody HotelBookingRequest hotelBookingRequest) {
        HotelBookingDto hotelBookingDto = hotelBookingService.createHotelBooking(hotelBookingRequest);
        return new ResponseEntity<>(hotelBookingDto, HttpStatus.CREATED);
    }
}
