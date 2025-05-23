package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import com.project.airbnb_app.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Override
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {
        Hotel hotel = getHotel(hotelId);

        log.debug("Convert RoomDto to Room and bind hotel to the room.");
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.info("Room successfully saved with the id: {}", room.getId());

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    public Boolean deleteRoom(Long hotelId, Long roomId) {
        if (!hotelRepository.existsById(hotelId)) {
            log.info("Hotel not found with the id: {}", hotelId);
            throw new ResourceNotFoundException("Hotel not found with the id: " + hotelId);
        }

        if (!roomRepository.existsById(roomId)) {
            log.info("Room not found with the id: {}", roomId);
            throw new ResourceNotFoundException("Room not found with the id: " + roomId);
        }

        roomRepository.deleteById(roomId);
        log.info("Room with the id {}  deleted succeefully.", roomId);
        return true;
    }

    @Override
    public RoomDto getRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.debug("Get the hotel info by id:  {}", roomId);
        Room room = roomRepository
                .findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the hotel id: " + hotelId + " room id: " + roomId));
        log.info("Room available with the hotel id: {} and room id: {}", hotelId, roomId);

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoomByHotelIdAndRoomId(Long hotelId, Long roomId, RoomDto roomDto) {
        Hotel hotel = getHotel(hotelId);

        log.debug("Updating RoomDto");
        roomDto.setId(roomId);
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.info("Room updated successfully");

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    private Hotel getHotel(Long hotelId) {
        log.info("Get hotel by hotel id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + hotelId));
        log.info("Hotel found with name of {}", hotel.getName());
        return hotel;
    }

    private Room getRoom(Long roomId) {
        log.info("Get room by room id: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + roomId));
        log.info("Room found with the room capacity of {}", room.getRoomCapacity());
        return room;
    }
}
