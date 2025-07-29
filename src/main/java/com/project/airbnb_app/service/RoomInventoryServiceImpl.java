package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.RoomInventoryDto;
import com.project.airbnb_app.dto.RoomInventoryRequestDto;
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
import java.util.stream.Collectors;

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
    public void createInventory(Long hotelId, Long roomId) {
        Hotel hotel = hotelDomainService.getHotelById(hotelId);

        log.debug("Fetch room by id {}", roomId);
        Room room = roomDomainService.getRoomByHotelIdAndRoomId(hotelId, roomId);
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

    @Override
    public void decreaseBookedRoomsCount(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount) {
        roomInventoryRepository.decreaseBookedRoomsCount(
                roomId,
                checkInDate,
                checkOutDate,
                roomsCount
        );
    }

    @Override
    @Transactional
    public void deleteInventoryByHotelIdAndRoomId(Long hotelId, Long roomId) {
        log.debug("Delete inventory with the hotel id: {} and room id: {}.", hotelId, roomId);
        roomInventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        log.debug("Delete inventory with the hotel id: {} and room id: {} is completed.", hotelId, roomId);
    }

    @Override
    public void findAndLockInventoryForModification(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount) {
        roomInventoryRepository.findAndLockInventoryForModification(
                roomId, checkInDate, checkOutDate, roomsCount
        );
    }

    @Transactional
    @Override
    public List<RoomInventoryDto> getRoomInventoryByRoomId(Long roomId) {
        Room room = roomDomainService.getRoomByRoomId(roomId);
        Hotel hotel = room.getHotel();

        hotelDomainService.validateHotelOwnership(hotel.getOwner().getId());

        List<RoomInventory> roomInventoryList = roomInventoryRepository.findAllByHotelAndRoom(hotel, room);

        return roomInventoryList.stream()
                .map((element) -> modelMapper.map(element, RoomInventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<RoomInventory> roomInventories) {
        roomInventoryRepository.saveAll(roomInventories);
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

    @Override
    public void updateBookedRoomsCount(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomsCount) {
        roomInventoryRepository.updateBookedRoomsCount(roomId, checkInDate, checkOutDate, roomsCount);
    }

    @Transactional
    @Override
    public List<RoomInventory> updateReservedRoomsCount(HotelBookingRequest hotelBookingRequest) {
        List<RoomInventory> roomInventoryList = findAndLockAvailableInventory(hotelBookingRequest);

        roomInventoryRepository.updateReservedRoomsCount(
                hotelBookingRequest.getRoomId(),
                hotelBookingRequest.getCheckInDate().toLocalDate(),
                hotelBookingRequest.getCheckOutDate().toLocalDate(),
                hotelBookingRequest.getBookedRoomsCount()
        );
        log.debug("Update reserve room count with the room inventory completed.");

        return roomInventoryList;
    }

    @Transactional
    @Override
    public void updateRoomInventory(Long roomId, RoomInventoryRequestDto roomInventoryRequestDto) {
        Room room = roomDomainService.getRoomByRoomId(roomId);

        hotelDomainService.validateHotelOwnership(room.getHotel().getOwner().getId());

        // Lock the inventory records for update
        roomInventoryRepository.findAndLockInventoryForUpdate(
                roomId,
                roomInventoryRequestDto.getStartDate(),
                roomInventoryRequestDto.getEndDate()
        );

        // Update the inventory records.
        roomInventoryRepository.updateRoomInventory(
                room.getId(),
                roomInventoryRequestDto.getSurgeFactor(),
                roomInventoryRequestDto.getStartDate(),
                roomInventoryRequestDto.getEndDate(),
                roomInventoryRequestDto.getClosed()
        );

        log.debug("Update room inventory completed.");
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
}
