package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.entity.Guest;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
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
    public List<GuestDto> createGuests(List<GuestDto> guestDtoList) {
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

    @Override
    public void deleteGuest(Long guestId) {
        if (guestRepository.existsById(guestId)) {
            guestRepository.deleteById(guestId);
        } else {
            throw new ResourceNotFoundException("Guest not found with the id: " + guestId);
        }
    }

    @Override
    public GuestDto getGuestById(Long guestId) {
        Guest guest = getGuestByIdOrThrow(guestId);
        return modelMapper.map(guest, GuestDto.class);
    }

    @Override
    public GuestDto updateGuest(Long guestId, GuestDto guestDto) {
        Guest guest = getGuestByIdOrThrow(guestId);

        if (guestDto.getName() != null) {
            guest.setName(guestDto.getName());
        }

        if (guestDto.getAge() != null) {
            guest.setAge(guestDto.getAge());
        }

        if (guestDto.getGender() != null) {
            guest.setGender(guestDto.getGender());
        }

        Guest savedGuest = guestRepository.save(guest);
        return modelMapper.map(savedGuest, GuestDto.class);
    }

    private Guest getGuestByIdOrThrow(Long guestId) {
        return guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with the id:" + guestId));
    }
}
