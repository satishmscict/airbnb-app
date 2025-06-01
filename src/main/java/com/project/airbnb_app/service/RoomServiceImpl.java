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

    private final HotelDomainService hotelDomainService;
    private final RoomInventoryService roomInventoryService;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Override
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {
        Hotel hotel = hotelDomainService.getHotelByIdAndIsActive(hotelId, true);

        log.debug("Convert RoomDto to Room and bind hotel to the room.");
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.debug("Room successfully saved with the id: {}", room.getId());

        log.debug("Check hotel active status: {} and generate inventory if hotel already activated.", hotel.getActive());
        if (hotel.getActive()) {
            roomInventoryService.createInventory(hotelId, savedRoom.getId());
            log.debug("Inventory generated for the room id: {}.", savedRoom.getId());
        }

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    @Transactional
    public Boolean deleteRoom(Long hotelId, Long roomId) {
        log.debug("Delete room started with the hotel id: {} and room id: {}.", hotelId, roomId);
        if (!isHotelExist(hotelId)) {
            log.debug("Hotel not found with the id: {}", hotelId);
            throw new ResourceNotFoundException("Hotel not found with the id: " + hotelId);
        }

        Room room = getRoomByHotelIdAndRoomId(hotelId, roomId);

        log.debug("Hotel and Room exist, continue deleting...");
        roomInventoryService.deleteInventoryByHotelIdAndRoomId(hotelId, room.getId());
        roomRepository.deleteByIdAndHotelId(room.getId(), hotelId);
        log.debug("Room with the id {}  and related inventories deleted successfully.", roomId);

        return true;
    }

    @Override
    public Room getRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.debug("Get room by hotel id: {} and room id: {}.", hotelId, roomId);

        Room room = roomRepository
                .findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the hotel id: " + hotelId +
                        " room id: " + roomId));
        log.debug("Room found with the room capacity of {}.", room.getRoomCapacity());
        return room;
    }

    @Override
    public RoomDto getRoomDtoByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.debug("Get the hotel info by id:  {}", roomId);
        Room room = getRoomByHotelIdAndRoomId(hotelId, roomId);
        log.debug("Room available with the hotel id: {} and room id: {}", hotelId, roomId);

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoomByHotelIdAndRoomId(Long hotelId, Long roomId, RoomDto roomDto) {
        log.debug("Start update room by hotel id: {} and room id: {}.", hotelId, roomId);
        Hotel hotel = hotelDomainService.getHotelByIdAndIsActive(hotelId, true);

        log.debug("Updating RoomDto.");
        roomDto.setId(roomId);
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.debug("Room updated successfully.");

        return modelMapper.map(savedRoom, RoomDto.class);
    }

    private Boolean isHotelExist(Long hotelId) {
        return hotelDomainService.isHotelExistById(hotelId);
    }
}
