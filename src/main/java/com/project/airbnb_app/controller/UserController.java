package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.ProfileUpdateRequest;
import com.project.airbnb_app.service.AppUserService;
import com.project.airbnb_app.service.HotelBookingOrchestratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final AppUserService appUserService;
    private final HotelBookingOrchestratorService hotelBookingOrchestratorService;

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<Map<String, String>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest profileUpdateRequest
    ) {
        appUserService.updateUserProfile(userId, profileUpdateRequest);
        return ResponseEntity.ok(Map.of("status", "Profile updated successfully"));
    }

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<List<HotelBookingDto>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(hotelBookingOrchestratorService.getAllBookingsByUserId());
    }

    @GetMapping("{userId}/profile")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(appUserService.getUserProfile(userId));
    }

}
