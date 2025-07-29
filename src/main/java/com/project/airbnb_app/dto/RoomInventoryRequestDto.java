package com.project.airbnb_app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RoomInventoryRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean closed;
    private BigDecimal surgeFactor;
}
