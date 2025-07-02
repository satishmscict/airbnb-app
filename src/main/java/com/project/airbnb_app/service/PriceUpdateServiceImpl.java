package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.HotelMinimumPrice;
import com.project.airbnb_app.entity.RoomInventory;
import com.project.airbnb_app.repository.HotelMinimumPriceRepository;
import com.project.airbnb_app.repository.HotelRepository;
import com.project.airbnb_app.repository.RoomInventoryRepository;
import com.project.airbnb_app.room_pricing_strategy.DynamicRoomPricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class PriceUpdateServiceImpl implements PriceUpdateService {

    private final int TOTAL_YEARS = 1;

    private final HotelMinimumPriceRepository hotelMinimumPriceRepository;
    private final HotelRepository hotelRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final DynamicRoomPricingService dynamicRoomPricingService;

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public void updateHotelPrice() {
        log.debug("Update hotel price starting.");
        int pageSize = 0;
        int batchSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(pageSize, batchSize));
            if (hotelPage.isEmpty()) {
                break;
            }

            hotelPage.getContent().forEach(this::updateHotelPrice);

            pageSize++;
        }
        log.debug("Update hotel price completed.");
    }

    private void updateHotelPrice(Hotel hotel) {
        log.debug("Update hotel price started for hotel id {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(TOTAL_YEARS);

        List<RoomInventory> roomInventoryList = roomInventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        updateInventoryPrice(roomInventoryList);

        updateHotelMiniumPrice(hotel, roomInventoryList);
        log.debug("Update hotel price completed.");
    }

    private void updateHotelMiniumPrice(
            Hotel hotel,
            List<RoomInventory> roomInventoryList
    ) {
        log.debug("Update hotel minimum price  started for hotel id {}", hotel.getId());
        // Compute minimum price per day of hotel. Get the room with the cheapest price from available room types and perform dynamic price calculation.
        Map<LocalDate, BigDecimal> cheapestRoomPriceMap = roomInventoryList
                .stream()
                .collect(Collectors.groupingBy(
                                RoomInventory::getDate,
                                Collectors.mapping(RoomInventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                        )
                ).entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, v -> v.getValue().orElse(BigDecimal.ZERO))
                );

        log.debug("cheapest room price map is : {}", cheapestRoomPriceMap);

        List<HotelMinimumPrice> hotelMinimumPricesList = new ArrayList<>();
        cheapestRoomPriceMap.forEach((localDate, roomPrice) -> {
            HotelMinimumPrice hotelMinimumPrice = hotelMinimumPriceRepository.findHotelByHotelAndDate(hotel, localDate).orElse(
                            new HotelMinimumPrice(hotel, localDate)
                    );

            hotelMinimumPrice.setPrice(roomPrice);
                    // Prepare hotel min price list for bulk insert.
                    hotelMinimumPricesList.add(hotelMinimumPrice);
                }
        );

        hotelMinimumPriceRepository.saveAll(hotelMinimumPricesList);
        log.debug("Hotel minimum price updated for {} inventories.", hotelMinimumPricesList.size());
    }

    private void updateInventoryPrice(List<RoomInventory> roomInventoryList) {
        log.debug("Update inventory price started for {} inventories.", roomInventoryList.size());
        roomInventoryList.forEach(roomInventory -> {
            BigDecimal dynamicPricing = dynamicRoomPricingService.calculateFinalPrice(roomInventory);
            roomInventory.setPrice(dynamicPricing);
        });

        roomInventoryRepository.saveAll(roomInventoryList);
        log.debug("Update inventory price completed");
    }
}
