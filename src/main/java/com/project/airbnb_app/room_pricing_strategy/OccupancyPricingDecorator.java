package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class OccupancyPricingDecorator implements RoomPricingStrategy {

    private static final double OCCUPANCY_MULTIPLIER = 1.2;

    private final RoomPricingStrategy baseRoomPricingStrategy;


    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        BigDecimal basePrice = baseRoomPricingStrategy.calculatePrice(roomInventory);
        log.debug("Room inventory base price: {}", basePrice);

        double occupancy = (double) roomInventory.getBookedRoomsCount() / roomInventory.getTotalRoomsCount();
        if (occupancy > 0.8) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(OCCUPANCY_MULTIPLIER));
        }
        log.debug("Final price from occupancy pricing decorator: {}", basePrice);

        return basePrice;
    }
}
