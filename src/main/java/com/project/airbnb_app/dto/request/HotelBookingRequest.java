package com.project.airbnb_app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotelBookingRequest {

    @NotNull(message = "Hotel id required.")
    private Long hotelId;

    @NotNull(message = "Room id required.")
    private Long roomId;

    @NotNull(message = "User id required.")
//    @JsonProperty("user")
//    private UserDto user;
    private Long userId;

    @NotNull(message = "Expected rooms count for booking is required.")
    private Integer bookedRoomsCount;

    @NotNull(message = "Check-in date is required.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInDate;

    @NotNull(message = "Check-out date is required.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutDate;
}
