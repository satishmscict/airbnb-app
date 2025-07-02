package com.project.airbnb_app.room_pricing_strategy;

import com.project.airbnb_app.entity.RoomInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class DynamicRoomPricingService {

    public BigDecimal calculateFinalPrice(RoomInventory roomInventory) {
        RoomPricingStrategy pricingStrategy = new BaseRoomPricing();

        //Apply the additional strategies.
        pricingStrategy = new SurgePricingDecorator(pricingStrategy);
        pricingStrategy = new OccupancyPricingDecorator(pricingStrategy);
        pricingStrategy = new UrgencyPricingDecorator(pricingStrategy);
        pricingStrategy = new HolidayPricingDecorator(pricingStrategy);

        return pricingStrategy.calculatePrice(roomInventory);
    }
}
