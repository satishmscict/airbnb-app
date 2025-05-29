package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final HotelService hotelService;
    private final InventoryService inventoryService;
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

        log.info("Check hotel active status: {} and generate inventory if hotel already activated.", hotel.getActive());
        if (hotel.getActive()) {
            inventoryService.createInventory(hotelId, savedRoom.getId());
            log.info("Inventory generated for the room id: {}.", savedRoom.getId());
        }

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    @Transactional
    public Boolean deleteRoom(Long hotelId, Long roomId) {
        log.info("Delete room started with the hotel id: {} and room id: {}.", hotelId, roomId);
        if (!isHotelExist(hotelId)) {
            log.info("Hotel not found with the id: {}", hotelId);
            throw new ResourceNotFoundException("Hotel not found with the id: " + hotelId);
        }

        Room room = getRoomByHotelIdAndRoomId(hotelId, roomId);

        log.info("Hotel and Room exist, continue deleting...");
        inventoryService.deleteInventoryByHotelIdAndRoomId(hotelId, room.getId());
        roomRepository.deleteByIdAndHotelId(room.getId(), hotelId);
        log.info("Room with the id {}  and related inventories deleted successfully.", roomId);

        return true;
    }

    @Override
    public RoomDto getRoomDtoByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.debug("Get the hotel info by id:  {}", roomId);
        Room room = getRoomByHotelIdAndRoomId(hotelId, roomId);
        log.info("Room available with the hotel id: {} and room id: {}", hotelId, roomId);

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoomByHotelIdAndRoomId(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Start update room by hotel id: {} and room id: {}.", hotelId, roomId);
        Hotel hotel = getHotel(hotelId);

        log.debug("Updating RoomDto.");
        roomDto.setId(roomId);
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.info("Room updated successfully.");

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    private Hotel getHotel(Long hotelId) {
        log.info("Get hotel by hotel id: {}.", hotelId);
        Hotel hotel = hotelService.getHotelById(hotelId);
        log.info("Hotel found with name of {}.", hotel.getName());
        return modelMapper.map(hotel, Hotel.class);
    }

    private Boolean isHotelExist(Long hotelId) {
        return hotelService.isHotelExistById(hotelId);
    }

    @Override
    public Room getRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.info("Get room by hotel id: {} and room id: {}.", hotelId, roomId);

        Room room = roomRepository
                .findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the hotel id: " + hotelId +
                        " room id: " + roomId));
        log.info("Room found with the room capacity of {}.", room.getRoomCapacity());
        return room;
    }
}
