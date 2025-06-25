package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class RoomPricingCalculatorService {

    private BigDecimal calculateDynamicPricing(RoomInventory roomInventory) {
        RoomPricingStrategy priceCalculator = new BaseRoomPricingStrategy();

        //Apply the additional strategies.
        priceCalculator = new SurgeRoomPricingDecorator(priceCalculator);
        priceCalculator = new OccupancyRoomPricingDecorator(priceCalculator);
        priceCalculator = new UrgencyRoomPricingDecorator(priceCalculator);
        priceCalculator = new HolidayRoomPricingDecorator(priceCalculator);

        return priceCalculator.calculatePrice(roomInventory);
    }
}
