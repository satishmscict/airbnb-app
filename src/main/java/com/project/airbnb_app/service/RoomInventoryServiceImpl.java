package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.entity.RoomInventory;
import com.project.airbnb_app.repository.RoomInventoryRepository;
import jakarta.persistence.OptimisticLockException;
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

    private final HotelDomainService hotelDomainService;
    private final ModelMapper modelMapper;
    private final RoomDomainService roomDomainService;
    private final RoomInventoryRepository roomInventoryRepository;

    private static RoomInventory buildInventory(Hotel hotel, Room room, LocalDate date) {
        return RoomInventory
                .builder()
                .hotel(hotel)
                .room(room)
                .date(date)
                .bookedRoomsCount(0)
                .reservedRoomsCount(0)
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
        log.debug("Delete inventory with the hotel id: {} and room id: {}.", hotelId, roomId);
        roomInventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        log.debug("Delete inventory with the hotel id: {} and room id: {} is completed.", hotelId, roomId);
    }

    @Override
    public Page<HotelDto> searchHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest) {
        log.debug("Find hotels by city: {}, start date: {} and end date: {} with total {} rooms.",
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount()
        );
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPageNo(), hotelSearchRequest.getPageSize());
        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());

        Page<Hotel> hotelsList = roomInventoryRepository.findHotelsByCityAndAvailability(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                daysCount,
                pageable
        );
        log.debug("Total {} hotels found.", hotelsList.getContent().size());

        return hotelsList.map((element) -> modelMapper.map(element, HotelDto.class));
    }

    @Transactional
    @Override
    public List<RoomInventory> updateReservedRoomsCount(HotelBookingRequest hotelBookingRequest) {
        List<RoomInventory> roomInventoryList = findAndLockAvailableInventory(hotelBookingRequest);

        for (RoomInventory roomInventory : roomInventoryList) {
            roomInventory.setReservedRoomsCount(roomInventory.getReservedRoomsCount() + hotelBookingRequest.getBookedRoomsCount());
        }
        roomInventoryList = roomInventoryRepository.saveAll(roomInventoryList);

        return roomInventoryList;
    }

    @Transactional
    private List<RoomInventory> findAndLockAvailableInventory(HotelBookingRequest hotelBookingRequest) {
        try {
            return roomInventoryRepository.findAndLockAvailableInventory(
                    hotelBookingRequest.getRoomId(),
                    hotelBookingRequest.getCheckInDate().toLocalDate(),
                    hotelBookingRequest.getCheckOutDate().toLocalDate(),
                    hotelBookingRequest.getBookedRoomsCount()
            );
        } catch (OptimisticLockException optimisticLockException) {
            throw new RuntimeException(optimisticLockException);
        }
    }

    @Override
    public void createInventory(Long hotelId, Long roomId) {
        Hotel hotel = hotelDomainService.getHotelById(hotelId);

        log.debug("Fetch room by id {}", roomId);
        Room room = roomDomainService.getRoomById(hotelId, roomId);
        log.debug("Room found with the id: {}", roomId);

        log.debug("Generate inventory for next {} days", TOTAL_INVENTORY_YEARS);
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusYears(TOTAL_INVENTORY_YEARS);

        List<RoomInventory> generateRoomInventory = new ArrayList<>();
        for (LocalDate date = currentDate; !date.isEqual(endDate); date = date.plusDays(1)) {
            RoomInventory roomInventory = buildInventory(hotel, room, date);
            generateRoomInventory.add(roomInventory);
        }
        roomInventoryRepository.saveAll(generateRoomInventory);

        log.debug("Successfully generate and save all the inventories, total {} inventories created.", generateRoomInventory.size());
    }
}
