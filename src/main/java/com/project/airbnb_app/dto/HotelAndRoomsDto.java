package com.project.airbnb_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class HotelAndRoomsDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
