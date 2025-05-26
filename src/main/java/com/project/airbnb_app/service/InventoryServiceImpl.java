package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.*;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Inventory;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import com.project.airbnb_app.repository.InventoryRepository;
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
public class InventoryServiceImpl implements InventoryService {

    private static final int TOTAL_INVENTORY_YEARS = 1;

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Override
    public Page<HotelDto> browseHotels(BrowseHotelRequest browseHotelRequest) {
        log.info("Browse hotel details by city: {}, start date: {} and end date: {} with total {} rooms.",
                browseHotelRequest.getCity(),
                browseHotelRequest.getStartDate(),
                browseHotelRequest.getEndDate(),
                browseHotelRequest.getRoomsCount()
        );
        Pageable pageable = PageRequest.of(browseHotelRequest.getPageNo(), browseHotelRequest.getPageSize());
        Long daysCount = ChronoUnit.DAYS.between(browseHotelRequest.getStartDate(), browseHotelRequest.getEndDate());

        Page<Hotel> inventory = inventoryRepository.findHotels(
                browseHotelRequest.getCity(),
                browseHotelRequest.getStartDate(),
                browseHotelRequest.getEndDate(),
                browseHotelRequest.getRoomsCount(),
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
        List<Inventory> inventoryList = inventoryRepository.saveAll(generateInventory);
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
        inventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        log.info("Delete inventory with the hotel id: {} and room id: {} is completed.", hotelId, roomId);
    }

    @Override
    public HotelInfoDto getHotelDetailsInfo(Long hotelId) {
        Hotel hotel = getHotel(hotelId);

        return HotelInfoDto
                .builder()
                .hotel(modelMapper.map(hotel, HotelDto.class))
                .rooms(hotel
                        .getRooms()
                        .stream()
                        .map((element) -> modelMapper.map(element, RoomDto.class))
                        .toList()
                )
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
