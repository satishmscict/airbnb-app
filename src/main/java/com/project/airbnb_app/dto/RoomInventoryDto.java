package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.Room;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RoomInventoryDto {

    private Long id;

    private HotelDto hotel;

    private Room room;

    private LocalDate date;

    private Integer bookedRoomsCount;

    private Integer totalRoomsCount;

    private BigDecimal surgeFactor;

    private BigDecimal price;

    private String city;

    private Boolean closed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
