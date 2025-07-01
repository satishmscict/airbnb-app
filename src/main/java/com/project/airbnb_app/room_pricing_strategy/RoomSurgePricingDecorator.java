package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class RoomSurgePricingDecorator implements RoomPricingStrategy {

    private final RoomPricingStrategy baseRoomPricingStrategy;

    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        return roomInventory
                .getSurgeFactor()
                .multiply(
                        baseRoomPricingStrategy.calculatePrice(roomInventory)
                );
    }
}
