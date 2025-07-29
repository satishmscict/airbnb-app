package com.project.airbnb_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HotelBookingReportResponseDto {
    private BigDecimal averageAmount;
    private Integer totalBookingCount;
    private BigDecimal totalRevenue;
}
