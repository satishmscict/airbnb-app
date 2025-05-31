package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.entity.RoomInventory;
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

    private static RoomInventory buildInventory(Hotel hotel, Room room, LocalDate date) {
        return RoomInventory
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
    @Transactional
    public void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.info("Delete inventory with the hotel id: {} and room id: {}.", hotelId, roomId);
        roomInventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        log.info("Delete inventory with the hotel id: {} and room id: {} is completed.", hotelId, roomId);
    }

    @Override
    public Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest) {
        log.info("Find hotels by city: {}, start date: {} and end date: {} with total {} rooms.",
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount()
        );
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPageNo(), hotelSearchRequest.getPageSize());
        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());

        Page<Hotel> inventory = roomInventoryRepository.findHotelsByCityAndAvailability(
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

    @Override
    public void createInventory(Long hotelId, Long roomId) {
        Hotel hotel = getHotel(hotelId);

        log.info("Fetch room by id {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the id: " + roomId));
        log.info("Room found with the id: {}", roomId);

        log.info("Generate inventory for next {} days", TOTAL_INVENTORY_YEARS);
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusYears(TOTAL_INVENTORY_YEARS);

        List<RoomInventory> generateRoomInventory = new ArrayList<>();
        for (LocalDate date = currentDate; !date.isEqual(endDate); date = date.plusDays(1)) {
            RoomInventory roomInventory = buildInventory(hotel, room, date);
            generateRoomInventory.add(roomInventory);
        }
        roomInventoryRepository.saveAll(generateRoomInventory);

        log.info("Successfully generate and save all the inventories, total {} inventories created.", generateRoomInventory.size());
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
