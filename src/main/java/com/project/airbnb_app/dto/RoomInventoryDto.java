package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RoomInventoryDto {

    private Long id;

    private Hotel hotel;

    private Room room;

    private LocalDate date;

    private Integer bookedRoomsCount;

    private Integer totalRoomsCount;

    private BigDecimal surgeFactor;

    private BigDecimal price; // surgeFactor * basePrice

    private String city;

    private Boolean closed;
}
