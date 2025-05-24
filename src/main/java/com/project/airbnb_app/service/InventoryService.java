package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.InventoryDto;

import java.util.List;

public interface InventoryService {

    List<InventoryDto> createInventory(Long hotelId, Long roomId);

    Boolean deleteInventory(Long hotelId, Long roomId);
}
