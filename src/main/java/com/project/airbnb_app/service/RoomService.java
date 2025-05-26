package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.RoomDto;

public interface RoomService {

    RoomDto createRoom(Long hotelId, RoomDto roomDto);

    Boolean deleteRoom(Long hotelId, Long roomId);

    RoomDto getRoomDtoByHotelIdAndRoomId(Long hotelId, Long roomId);

    RoomDto updateRoomByHotelIdAndRoomId(Long hotelId, Long roomId, RoomDto roomDto);
}
