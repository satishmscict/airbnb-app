package com.project.airbnb_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
