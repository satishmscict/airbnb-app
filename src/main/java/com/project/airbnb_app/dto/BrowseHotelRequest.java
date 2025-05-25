package com.project.airbnb_app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BrowseHotelRequest {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomsCount;
    private Integer pageNo = 0;
    private Integer pageSize = 10;
}
