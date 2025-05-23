package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId, @RequestBody @Valid RoomDto roomDto) {
        RoomDto savedRoomDto = roomService.createRoom(hotelId, roomDto);
        return new ResponseEntity<>(savedRoomDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{roomId}/hotels/{hotelId}")
    public ResponseEntity<Boolean> deleteRoomByHotelIdAndRoomId(
            @PathVariable Long roomId,
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(roomService.deleteRoom(hotelId, roomId));

    }

    @GetMapping("/{roomId}/hotels/{hotelId}")
    public ResponseEntity<RoomDto> getRoomByHotelIdAndRoomId(
            @PathVariable Long hotelId,
            @PathVariable Long roomId
    ) {
        RoomDto roomDto = roomService.getRoomByHotelIdAndRoomId(hotelId, roomId);
        return ResponseEntity.ok(roomDto);
    }

    @PutMapping("/{roomId}/hotels/{hotelId}")
    public ResponseEntity<RoomDto> updateRoomByHotelIdAndRoomId(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @RequestBody @Valid RoomDto roomDto
    ) {
        RoomDto updatedRoomDto = roomService.updateRoomByHotelIdAndRoomId(hotelId, roomId, roomDto);
        return ResponseEntity.ok(updatedRoomDto);
    }
}
