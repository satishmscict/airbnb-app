package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.HotelBookingRequest;
import com.project.airbnb_app.entity.*;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.entity.enums.Role;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.AppUserRepository;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingServiceImpl implements HotelBookingService {

    private final HotelBookingRepository hotelBookingRepository;
    private final HotelService hotelService;
    private final ModelMapper modelMapper;
    private final RoomService roomService;

    // TODO: Refactor to use AppUserService and clean up related code.
    private final AppUserRepository appUserRepository;
    // TODO: Refactor to use GuestService and clean up related code.
    private final GuestRepository guestRepository;

    @Override
    public HotelBookingDto crateBooking(HotelBookingRequest hotelBookingRequest) {
        try {
            log.info("Create booking request started with {}", hotelBookingRequest.toString());

            Hotel hotel = hotelService.getHotelById(hotelBookingRequest.getHotelId());
            Room room = roomService.getRoomByHotelIdAndRoomId(
                    hotelBookingRequest.getRoomId(),
                    hotelBookingRequest.getHotelId()
            );
            User user = getAppUser();

            Set<Guest> guestSet = getGuestSet(hotelBookingRequest.getGuest(), user);
            guestSet = guestRepository.saveAll(guestSet).stream().collect(Collectors.toSet());
            log.info("Guest dto converted into Guest. Total {} guest saved and available.", guestSet.size());

            log.info("Booking object preparing....");
            HotelBooking hotelBooking = HotelBooking
                    .builder()
                    .hotel(hotel)
                    .room(room)
                    .user(user)
                    .bookingStatus(BookingStatus.RESERVED)
                    .guest(guestSet)
                    .checkInDate(hotelBookingRequest.getCheckInDate())
                    .checkOutDate(hotelBookingRequest.getCheckOutDate())
                    .roomsCount(hotelBookingRequest.getBookedRoomsCount())
                    .build();

            HotelBooking savedHotelBooking = hotelBookingRepository.save(hotelBooking);
            log.info("Booking object prepared and saved with the id : {}", hotelBooking.getId());

            return modelMapper.map(savedHotelBooking, HotelBookingDto.class);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Hotel booking failed : " + e.getCause());
        }
    }

    private User getAppUser() {
        Set<Role> roleSet = EnumSet.of(Role.GUEST);

        User user = appUserRepository.findById(1L).orElse(null);
        if (user == null) {
            user = User
                    .builder()
                    .email("satish@gmail.com")
                    .name("Satish")
                    .roles(roleSet)
                    .password("sa@1234")
                    .build();
            user = appUserRepository.save(user);
        }

        return user;
    }

    private Set<Guest> getGuestSet(Set<GuestDto> guestDtoSet, User user) {
        return guestDtoSet
                .stream()
                .map((guestDto) -> {
                            guestDto.setUser(user);
                            return modelMapper.map(guestDto, Guest.class);
                        }
                )
                .collect(Collectors.toSet());
    }
}
