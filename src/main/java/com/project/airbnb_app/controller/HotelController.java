package com.project.airbnb_app.controller;

import com.project.airbnb_app.advice.ApiResponse;
import com.project.airbnb_app.dto.HotelAndRoomsDto;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.dto.request.HotelMiniumPriceRequest;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.service.HotelOrchestratorService;
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

    private final HotelOrchestratorService hotelOrchestratorService;
    private final HotelService hotelService;

    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelOrchestratorService.activateHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid HotelDto hotelDto) {
        return new ResponseEntity<>(hotelService.createHotel(hotelDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<String>> deleteHotelById(@PathVariable Long hotelId) {
        String result = hotelOrchestratorService.deleteHotelWithDependencies(hotelId);
        ApiResponse<String> apiResponse = new ApiResponse<>(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelByIdAndIsActive(hotelId));
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<HotelAndRoomsDto> getHotelAndRoomsDetails(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelAndRoomsDetails(hotelId));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@Valid @RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelDto> hotelDto = hotelOrchestratorService.searchHotelsByCityAndAvailability(hotelSearchRequest);
        return ResponseEntity.ok(hotelDto);
    }

    @PostMapping("/search-with-minimum-price")
    public ResponseEntity<Page<HotelMinimumPriceDto>> searchHotelsWithMinimumPrice(@Valid @RequestBody HotelMiniumPriceRequest hotelMiniumPriceRequest) {
        Page<HotelMinimumPriceDto> hotels = hotelOrchestratorService.searchHotelsByCityWithMiniumPrice(hotelMiniumPriceRequest);
        return ResponseEntity.ok(hotels);
    }
}
