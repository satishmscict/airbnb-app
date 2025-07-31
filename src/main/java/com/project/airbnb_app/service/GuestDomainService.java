package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Guest;
import com.project.airbnb_app.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuestDomainService {

    private final GuestRepository guestRepository;

    public List<Guest> findGuestByIds(List<Long> guestIds) {
        return guestRepository.findAllById(guestIds);
    }
}
