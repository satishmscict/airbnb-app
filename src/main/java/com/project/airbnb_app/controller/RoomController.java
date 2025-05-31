package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/hotels/{hotelId}/rooms")
@Tag(name = "Rooms API")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(
            @PathVariable Long hotelId,
            @RequestBody @Valid RoomDto roomDto
    ) {
        RoomDto savedRoomDto = roomService.createRoom(hotelId, roomDto);
        return new ResponseEntity<>(savedRoomDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Boolean> deleteRoomByHotelIdAndRoomId(
            @PathVariable Long roomId,
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(roomService.deleteRoom(hotelId, roomId));

    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomByHotelIdAndRoomId(
            @PathVariable Long hotelId,
            @PathVariable Long roomId
    ) {
        RoomDto roomDto = roomService.getRoomDtoByHotelIdAndRoomId(hotelId, roomId);
        return ResponseEntity.ok(roomDto);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomByHotelIdAndRoomId(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @RequestBody @Valid RoomDto roomDto
    ) {
        RoomDto updatedRoomDto = roomService.updateRoomByHotelIdAndRoomId(hotelId, roomId, roomDto);
        return ResponseEntity.ok(updatedRoomDto);
    }
}
