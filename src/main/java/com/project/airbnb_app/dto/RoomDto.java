package com.project.airbnb_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.airbnb_app.entity.Hotel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomDto {

    private Long id;

    @JsonIgnore
    private Hotel hotel;

    @NotBlank(message = "Room type is required.")
    private String type;

    @Positive(message = "Base room price is required")
    private BigDecimal basePrice;

    @NotEmpty(message = "Amenities required.")
    private String[] amenities;

    @NotEmpty(message = "Photos required.")
    private String[] photos;

    @Positive(message = "Total rooms count required.")
    private Integer totalRoomCount;

    @Positive(message = "Room capacity for members is required.")
    private Integer roomCapacity;
}
