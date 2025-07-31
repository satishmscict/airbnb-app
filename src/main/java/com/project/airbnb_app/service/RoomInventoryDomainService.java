package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.RoomInventory;
import com.project.airbnb_app.repository.RoomInventoryRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class RoomInventoryDomainService {

    private final RoomInventoryRepository roomInventoryRepository;

    List<RoomInventory> findByHotelAndDateBetween(
            Hotel hotel,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return roomInventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
    }

    @Transactional
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
