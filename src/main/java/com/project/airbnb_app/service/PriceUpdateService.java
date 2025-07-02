package com.project.airbnb_app.service;

public interface PriceUpdateService {

    // Scheduler to update the room inventory and hotel minimum price of every hotel.
    void updateHotelPrice();

    // No use, just added to check default methods.
    default int getDefaultInterval() {
        return 1;
    }
}
