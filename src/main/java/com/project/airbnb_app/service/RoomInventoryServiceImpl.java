package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.*;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Inventory;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import com.project.airbnb_app.repository.RoomInventoryRepository;
import com.project.airbnb_app.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomInventoryServiceImpl implements RoomInventoryService {

    private static final int TOTAL_INVENTORY_YEARS = 1;

    // TODO: Refactor to use HotelService and clean up related code.
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final RoomInventoryRepository roomInventoryRepository;
    // TODO: Refactor to use RoomService and clean up related code.
    private final RoomRepository roomRepository;

    @Override
    public List<InventoryDto> createInventory(Long hotelId, Long roomId) {
        Hotel hotel = getHotel(hotelId);

        log.info("Fetch room by id {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the id: " + roomId));
        log.info("Room found with the id: {}", roomId);

        log.info("Generate inventory for next {} days", TOTAL_INVENTORY_YEARS);
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusYears(TOTAL_INVENTORY_YEARS);

        List<Inventory> generateInventory = new ArrayList<>();
        for (LocalDate date = currentDate; !date.isEqual(endDate); date = date.plusDays(1)) {
            Inventory inventory = buildInventory(hotel, room, date);
            generateInventory.add(inventory);
        }
        List<Inventory> inventoryList = roomInventoryRepository.saveAll(generateInventory);
        log.info("Successfully generate and save all the inventories, total {} inventories created.", generateInventory.size());

        return inventoryList
                .stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.info("Delete inventory with the hotel id: {} and room id: {}.", hotelId, roomId);
        roomInventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        log.info("Delete inventory with the hotel id: {} and room id: {} is completed.", hotelId, roomId);
    }

    @Override
    public HotelInfoDto getHotelDetailsInfo(Long hotelId) {
        Hotel hotel = getHotel(hotelId);

        List<RoomDto> roomDtoList = hotel
                .getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return HotelInfoDto
                .builder()
                .hotel(modelMapper.map(hotel, HotelDto.class))
                .rooms(roomDtoList)
                .build();
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Browse hotel details by city: {}, start date: {} and end date: {} with total {} rooms.",
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount()
        );
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPageNo(), hotelSearchRequest.getPageSize());
        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());

        Page<Hotel> inventory = roomInventoryRepository.findHotels(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                daysCount,
                pageable
        );
        log.info("Total {} hotels found.", inventory.getContent().size());

        return inventory.map((element) -> modelMapper.map(element, HotelDto.class));
    }

    private static Inventory buildInventory(Hotel hotel, Room room, LocalDate date) {
        return Inventory
                .builder()
                .hotel(hotel)
                .room(room)
                .date(date)
                .bookedRoomsCount(0)
                .totalRoomsCount(room.getTotalRoomCount())
                .surgeFactor(BigDecimal.ONE)
                .price(BigDecimal.ONE.multiply(room.getBasePrice()))
                .city(hotel.getCity())
                .closed(false)
                .build();
    }

    private Hotel getHotel(Long hotelId) {
        log.info("Get hotel by id {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + hotelId));
        log.info("Hotel found with the name: {}", hotel.getName());
        return hotel;
    }
}
