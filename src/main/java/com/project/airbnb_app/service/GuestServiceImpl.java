package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.entity.Guest;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.repository.GuestRepository;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final HotelBookingRepository hotelBookingRepository;

    @Override
    public List<GuestDto> addGuests(User user, List<GuestDto> guestDtoList) {
        log.info("Add guest started with {} guests.", guestDtoList.size());
        List<Guest> guestList = guestDtoList
                .stream()
                .map((element) -> {
                            element.setUser(user);
                            return modelMapper.map(element, Guest.class);
                        }
                )
                .toList();

        guestList = guestRepository.saveAll(guestList);
        log.info("Saved all guests details.");

        guestDtoList = guestList.stream().map((element) -> modelMapper.map(element, GuestDto.class)).toList();

        return guestDtoList;
    }
}
