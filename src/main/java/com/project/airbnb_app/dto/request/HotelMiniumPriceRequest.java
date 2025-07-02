package com.project.airbnb_app.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelMiniumPriceRequest {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNo = 0;
    private Integer pageSize = 10;
}
