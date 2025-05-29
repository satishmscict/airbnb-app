package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.entity.Room;

public interface RoomService {

    RoomDto createRoom(Long hotelId, RoomDto roomDto);

    Boolean deleteRoom(Long hotelId, Long roomId);

    Room getRoomByHotelIdAndRoomId(Long hotelId, Long roomId);

    RoomDto getRoomDtoByHotelIdAndRoomId(Long hotelId, Long roomId);

    RoomDto updateRoomByHotelIdAndRoomId(Long hotelId, Long roomId, RoomDto roomDto);
}
