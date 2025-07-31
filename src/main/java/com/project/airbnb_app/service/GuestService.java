package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;

import java.util.List;

public interface GuestService {

    List<GuestDto> createGuests(List<GuestDto> guestDtoList);

    void deleteGuest(Long guestId);

    GuestDto getGuestById(Long guestId);

    GuestDto updateGuest(Long guestId, GuestDto guestDto);
}
