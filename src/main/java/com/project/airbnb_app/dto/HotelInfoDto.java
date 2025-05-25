package com.project.airbnb_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotBlank
@Data
public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
