package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.request.GuestCreateRequest;
import com.project.airbnb_app.service.GuestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
@Tag(name = "Guest")
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    ResponseEntity<List<GuestDto>> createGuest(@RequestBody GuestCreateRequest guestCreateRequest) {
        return ResponseEntity.ok(guestService.createGuests(guestCreateRequest.getGuests()));
    }

    @DeleteMapping("/{guestId}")
    ResponseEntity<Map<String, String>> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.ok(Map.of("status", "Guest deleted successfully."));
    }

    @GetMapping("/{guestId}")
    ResponseEntity<GuestDto> getGuest(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.getGuestById(guestId));
    }

    @PatchMapping("/{guestId}")
    ResponseEntity<GuestDto> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        return ResponseEntity.ok(guestService.updateGuest(guestId, guestDto));
    }
}
