package com.project.airbnb_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelMinimumPriceDto {
    private Long hotelId;
    private String hotelName;
    private Double avgPrice;
}
