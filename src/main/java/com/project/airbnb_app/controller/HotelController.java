package com.project.airbnb_app.controller;

import com.project.airbnb_app.advice.ApiResponse;
import com.project.airbnb_app.dto.HotelAndRoomsDto;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.service.HotelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel API")
public class HotelController {

    private final HotelService hotelService;

    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.activateHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid HotelDto hotelDto) {
        return new ResponseEntity<>(hotelService.createHotel(hotelDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<String>> deleteHotelById(@PathVariable Long hotelId) {
        String result = hotelService.deleteHotelById(hotelId);
        ApiResponse<String> apiResponse = new ApiResponse<>(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelDtoByIdAndIsActive(hotelId));
    }

    @GetMapping("/{hotelId}/getHotelAndRooms")
    public ResponseEntity<HotelAndRoomsDto> getHotelAndRoomsDetails(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelAndRoomsDetails(hotelId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody @Valid HotelSearchRequest hotelSearchRequest) {
        Page<HotelDto> hotelDto = hotelService.findHotelsByCityAndAvailability(hotelSearchRequest);
        return ResponseEntity.ok(hotelDto);
    }
}
