package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.BrowseHotelRequest;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelInfoDto;
import com.project.airbnb_app.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels")
@Tag(name = "Search Hotel API")
public class BrowseHotelController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> browseHotels(@RequestBody @Valid BrowseHotelRequest browseHotelRequest) {
        Page<HotelDto> hotelDto = inventoryService.browseHotels(browseHotelRequest);
        return ResponseEntity.ok(hotelDto);
    }

    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<HotelInfoDto> getHotelDetailsInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(inventoryService.getHotelDetailsInfo(hotelId));
    }
}
