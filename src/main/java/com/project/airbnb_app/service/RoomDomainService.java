package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomDomainService {

    private final RoomRepository roomRepository;

    Room getRoomById(Long hotelId, Long roomId) {
        log.debug("Fetch room by hotel id: {} and room id: {} ", hotelId, roomId);
        Room room = roomRepository
                .findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the hotel id: " + hotelId +
                        " room id: " + roomId));
        log.debug("Room found with the hotel id: {} and room id: {}", hotelId, roomId);
        return room;
    }


}
