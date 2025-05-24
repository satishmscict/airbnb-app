package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.HotelContactInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HotelDto {
    private Long id;

    @NotBlank(message = "Hotel name is required.")
    @Size(min = 5, max = 100, message = "The hotel name should be a minimum of 5 and a maximum of 100 characters long.")
    private String name;

    @NotBlank(message = "City is required.")
    @Size(min = 3, max = 50, message = "The city name should be a minimum of 3 and a maximum of 50 characters long.")
    private String city;

    @NotNull(message = "Contact info is required.")
    private HotelContactInfo hotelContactInfo;

    private String[] photos;

    private String[] amenities;

    private Boolean active;
}
