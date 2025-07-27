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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
@Tag(name = "Hotel Booking API")
public class HotelBookingController {

    private final HotelBookingService hotelBookingService;

    @PostMapping("/{bookingId}/guests")
    ResponseEntity<List<GuestDto>> addGuestsToBooking(@PathVariable Long bookingId, @RequestBody GuestCreateDto guestCreate) {
        return new ResponseEntity<>(
                hotelBookingService.addGuestsToBooking(
                        bookingId,
                        guestCreate.getGuest()
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{bookingId}/cancel")
    ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long bookingId) {
        hotelBookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(Map.of("status", String.format("Booking cancelled with th bookingId: %s", bookingId)));
    }

    @PostMapping
    public ResponseEntity<HotelBookingDto> createBooking(@Valid @RequestBody HotelBookingRequest hotelBookingRequest) {
        HotelBookingDto hotelBookingDto = hotelBookingService.createHotelBooking(hotelBookingRequest);
        return new ResponseEntity<>(hotelBookingDto, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}/status")
    ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of("bookingStatus", hotelBookingService.getBookingStatus(bookingId)));
    }

    @GetMapping("/{bookingId}/initPayment")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId) {
        String paymentSessionUrl = hotelBookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("paymentSessionUrl", paymentSessionUrl));
    }
}
