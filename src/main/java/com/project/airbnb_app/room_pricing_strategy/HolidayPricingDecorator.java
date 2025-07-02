package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class HolidayPricingDecorator implements RoomPricingStrategy {

    private static final double HOLIDAY_MULTIPLIER = 1.25;

    private final RoomPricingStrategy baseRoomPricingStrategy;

    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        BigDecimal basePrice = baseRoomPricingStrategy.calculatePrice(roomInventory);
        log.debug("Room inventory base price: {}", basePrice);

        boolean isTodayHoliday = true; // Need to check with third party API.
        if (isTodayHoliday) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(HOLIDAY_MULTIPLIER));
        }
        log.debug("Final price from holiday pricing decorator: {}", basePrice);

        return basePrice;
    }
}
