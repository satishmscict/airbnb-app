package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.entity.Guest;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final AppUserDomainService appUserDomainService;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> addGuests(List<GuestDto> guestDtoList) {
        log.debug("Add guest started with {} guests.", guestDtoList.size());

        User user = appUserDomainService.getCurrentUser();

        List<Guest> guestList = guestDtoList
                .stream()
                .map((element) -> {
                            element.setUser(user);
                            return modelMapper.map(element, Guest.class);
                        }
                )
                .toList();

        guestList = guestRepository.saveAll(guestList);
        log.debug("Saved all guests details.");

        guestDtoList = guestList.stream()
                .map((element) -> modelMapper.map(element, GuestDto.class))
                .toList();

        return guestDtoList;
    }
}
