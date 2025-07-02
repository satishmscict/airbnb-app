package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;

import java.math.BigDecimal;

public interface RoomPricingStrategy {

    BigDecimal calculatePrice(RoomInventory roomInventory);
}
