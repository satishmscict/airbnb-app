package com.project.airbnb_app.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RoomInventoryRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean closed;
    private BigDecimal surgeFactor;
}
