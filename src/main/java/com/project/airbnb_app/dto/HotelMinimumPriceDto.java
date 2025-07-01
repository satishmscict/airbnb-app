package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelMinimumPriceDto {
    // TODO: Possibility to avoid entity here.
    private Hotel hotel;
    private double price;
}
