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

    private static final int BATCH_SIZE = 100;
    private static final int TOTAL_YEARS = 1;

    private final HotelMinimumPriceRepository hotelMinimumPriceRepository;
    private final HotelRepository hotelRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final DynamicRoomPricingService dynamicRoomPricingService;

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public void updateHotelPrice() {
        log.debug("Starting scheduled hotel price update.");
        int pageSize = 0;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(pageSize, BATCH_SIZE));
            if (hotelPage.isEmpty()) {
                break;
            }

            hotelPage.getContent().forEach(this::updatePriceForHotel);
            pageSize++;
        }
    }

    private void updatePriceForHotel(Hotel hotel) {
        log.debug("Updating price for hotel Id: {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(TOTAL_YEARS);

        List<RoomInventory> roomInventoryList = roomInventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateRoomInventoryPrices(roomInventoryList);
        updateMinimumHotelPrices(hotel, roomInventoryList);
    }

    private void updateMinimumHotelPrices(
            Hotel hotel,
            List<RoomInventory> roomInventories
    ) {
        log.debug("Updating minimum prices for hotel Id: {}", hotel.getId());

        // Compute minimum price per day of hotel. Get the room with the cheapest price from available room types and perform dynamic price calculation.
        Map<LocalDate, BigDecimal> minimumPricePerDayMap = roomInventories.stream()
                .collect(Collectors.groupingBy(
                                RoomInventory::getDate,
                                Collectors.mapping(RoomInventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                        )
                ).entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, v -> v.getValue().orElse(BigDecimal.ZERO))
                );

        log.debug("cheapest room price map is : {}", minimumPricePerDayMap);

        List<HotelMinimumPrice> hotelMinimumPricesList = new ArrayList<>();
        minimumPricePerDayMap.forEach((localDate, roomPrice) -> {
            HotelMinimumPrice hotelMinimumPrice = hotelMinimumPriceRepository.findHotelByHotelAndDate(hotel, localDate).orElse(
                            new HotelMinimumPrice(hotel, localDate)
                    );

            hotelMinimumPrice.setPrice(roomPrice);
                    hotelMinimumPricesList.add(hotelMinimumPrice);
                }
        );

        hotelMinimumPriceRepository.saveAll(hotelMinimumPricesList);
        log.debug("Hotel minimum prices updated for {} days.", hotelMinimumPricesList.size());
    }

    private void updateRoomInventoryPrices(List<RoomInventory> roomInventories) {
        log.debug("Updating dynamic prices for {} inventories.", roomInventories.size());
        roomInventories.forEach(roomInventory -> {
            BigDecimal dynamicPricing = dynamicRoomPricingService.calculateFinalPrice(roomInventory);
            roomInventory.setPrice(dynamicPricing);
        });

        roomInventoryRepository.saveAll(roomInventories);
        log.debug("Dynamic prices updated successfully.");
    }
}
