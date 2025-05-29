package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long Id;

    private HotelDto hotel;

    private RoomDto room;

    private UserDto user;

    private Integer roomsCount;

    private BookingStatus bookingStatus;

    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;

    private Set<GuestDto> guest;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return Objects.equals(Id, that.Id) && Objects.equals(roomsCount, that.roomsCount) && Objects.equals(checkInDate, that.checkInDate) && Objects.equals(checkOutDate, that.checkOutDate);
    }
}
