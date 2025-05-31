package com.project.airbnb_app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class HotelBookingRequest {

    @NotNull(message = "Hotel id required.")
    private Long hotelId;

    @NotNull(message = "Room id required.")
    private Long roomId;

    @NotNull(message = "User details required.")
    @JsonProperty("user")
    private UserDto user;

    @NotNull(message = "Expected rooms count for booking is required.")
    private Integer bookedRoomsCount;

    @NotNull(message = "Check-in date is required.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInDate;

    @NotNull(message = "Check-out date is required.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutDate;

    @NotNull(message = "Guest details required.")
    private Set<GuestDto> guest;
}
