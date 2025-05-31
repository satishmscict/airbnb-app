package com.project.airbnb_app.dto.request;

import com.project.airbnb_app.dto.GuestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GuestCreateDto {
    private Long bookingId;
    private List<GuestDto> guest;
}
