package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayRoomPricingDecorator implements RoomPricingStrategy {

    private static final double HOLIDAY_MULTIPLIER = 1.25;

    private final RoomPricingStrategy baseRoomPricingStrategy;

    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        BigDecimal basePrice = baseRoomPricingStrategy.calculatePrice(roomInventory);

        boolean isTodayHoliday = true; // Need to check with third party API.
        if (isTodayHoliday) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(HOLIDAY_MULTIPLIER));
        }

        return basePrice;
    }
}
