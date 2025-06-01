package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.entity.User;

import java.util.List;

public interface GuestService {

    List<GuestDto> addGuests(User user, List<GuestDto> guestDtoList);
}
