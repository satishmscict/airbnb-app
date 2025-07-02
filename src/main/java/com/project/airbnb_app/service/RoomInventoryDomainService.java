package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.RoomInventory;
import com.project.airbnb_app.repository.RoomInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RoomInventoryDomainService {

    private final RoomInventoryRepository roomInventoryRepository;

    List<RoomInventory> findByHotelAndDateBetween(
            Hotel hotel,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return roomInventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
    }
}
