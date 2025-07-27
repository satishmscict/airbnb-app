package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;

import java.util.List;

public interface GuestService {

    List<GuestDto> addGuests(List<GuestDto> guestDtoList);
}
