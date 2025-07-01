package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;

import java.math.BigDecimal;

public class RoomBasePricingStrategy implements RoomPricingStrategy {

    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        return roomInventory.getRoom().getBasePrice();
    }
}
