package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class RoomPricingCalculatorService {

    private BigDecimal calculateDynamicPricing(RoomInventory roomInventory) {
        RoomPricingStrategy priceCalculator = new RoomBasePricingStrategy();

        //Apply the additional strategies.
        priceCalculator = new RoomSurgePricingDecorator(priceCalculator);
        priceCalculator = new RoomOccupancyPricingDecorator(priceCalculator);
        priceCalculator = new RoomUrgencyPricingDecorator(priceCalculator);
        priceCalculator = new RoomHolidayPricingDecorator(priceCalculator);

        return priceCalculator.calculatePrice(roomInventory);
    }
}
