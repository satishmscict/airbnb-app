package com.project.airbnb_app.controller;

import com.project.airbnb_app.dto.RoomInventoryDto;
import com.project.airbnb_app.dto.RoomInventoryRequestDto;
import com.project.airbnb_app.service.RoomInventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/inventory")
@Tag(name = "Room Inventory")
public class RoomInventoryController {

    private final RoomInventoryService roomInventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<RoomInventoryDto>> getRoomInventoryByHotelIdAndRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomInventoryService.getRoomInventoryByRoomId(roomId));
    }

    @PatchMapping("rooms/{roomId}")
    public ResponseEntity<Map<String, String>> updateRoomInventory(@PathVariable Long roomId, @RequestBody RoomInventoryRequestDto roomInventoryRequestDto) {
        roomInventoryService.updateRoomInventory(roomId, roomInventoryRequestDto);
        return ResponseEntity.ok(Map.of("status", "Inventory updated successfully."));
    }
}
