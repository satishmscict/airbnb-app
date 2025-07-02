package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class UrgencyPricingDecorator implements RoomPricingStrategy {

    private static final int URGENCY_DAYS_LIMIT = 7;
    private static final double URGENCY_MULTIPLIER = 1.15;

    private final RoomPricingStrategy baseRoomPricingStrategy;

    @Override
    public BigDecimal calculatePrice(RoomInventory roomInventory) {
        BigDecimal basePrice = baseRoomPricingStrategy.calculatePrice(roomInventory);
        log.debug("Room inventory base price: {}", basePrice);

        LocalDate currentDate = LocalDate.now();
        if (!roomInventory.getDate().isBefore(currentDate)
                && roomInventory.getDate().isBefore(currentDate.plusDays(URGENCY_DAYS_LIMIT))
        ) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(URGENCY_MULTIPLIER));
        }
        log.debug("Final price from urgency pricing decorator: {}", basePrice);

        return basePrice;
    }
}
