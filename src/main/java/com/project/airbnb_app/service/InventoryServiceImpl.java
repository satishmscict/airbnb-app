package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.InventoryDto;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final int TOTAL_INVENTORY_DAYS = 365;

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Override
    public List<InventoryDto> createInventory(Long hotelId, Long roomId) {
        log.info("Fetch hotel by id {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the given id: " + hotelId));
        log.info("Hotel found with the name: {}", hotel.getName());

        log.info("Fetch room by id {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with the id: " + roomId));
        log.info("Room fonund with the id: {}", roomId);

        log.info("Generate inventory for next {} days", TOTAL_INVENTORY_DAYS);
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(TOTAL_INVENTORY_DAYS);

        List<Inventory> generateInventory = new ArrayList<>();
        for (LocalDate date = currentDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = Inventory
                    .builder()
                    .hotel(hotel)
                    .room(room)
                    .date(date)
                    .bookedRoomsCount(0)
                    .totalRoomsCount(room.getTotalRoomCount())
                    .surgeFactor(BigDecimal.ONE)
                    .price(BigDecimal.ONE.multiply(room.getBasePrice()))
                    .city(room.getHotel().getCity())
                    .closed(false)
                    .build();
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
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Boolean deleteInventory(Long hotelId, Long roomId) {
        inventoryRepository.deleteAllByHotelIdAndRoomId(hotelId, roomId);
        return true;
    }
}
