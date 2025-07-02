package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingDecorator implements RoomPricingStrategy {

    private static final double OCCUPANCY_MULTIPLIER = 1.2;

    private final RoomPricingStrategy baseRoomPricingStrategy;


    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        BigDecimal basePrice = baseRoomPricingStrategy.calculatePrice(roomInventory);

        double occupancy = (double) roomInventory.getBookedRoomsCount() / roomInventory.getTotalRoomsCount();
        if (occupancy > 0.8) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(OCCUPANCY_MULTIPLIER));
        }

        return basePrice;
    }
}
