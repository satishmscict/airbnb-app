package com.project.airbnb_app.controller;

import com.project.airbnb_app.advice.ApiResponse;
import com.project.airbnb_app.dto.BrowseHotelRequest;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelInfoDto;
import com.project.airbnb_app.service.HotelService;
import com.project.airbnb_app.service.RoomInventoryService;
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
@Tag(name = "Admin Hotel API")
public class HotelController {

    private final HotelService hotelService;
    private final RoomInventoryService roomInventoryService;

    @PatchMapping("/{hotelId}")
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
    public ResponseEntity<HotelDto> getHotelDetailsById(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelDtoById(hotelId));
    }

    @GetMapping("/{hotelId}/getHotelDetails")
    public ResponseEntity<HotelInfoDto> getHotelDetailsInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomInventoryService.getHotelDetailsInfo(hotelId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody @Valid BrowseHotelRequest browseHotelRequest) {
        Page<HotelDto> hotelDto = roomInventoryService.searchHotels(browseHotelRequest);
        return ResponseEntity.ok(hotelDto);
    }
}
