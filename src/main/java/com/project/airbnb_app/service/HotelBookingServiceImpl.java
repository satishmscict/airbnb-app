package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.BookingDto;
import com.project.airbnb_app.dto.HotelBookingRequest;
import com.project.airbnb_app.entity.*;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.entity.enums.Role;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.AppUserRepository;
import com.project.airbnb_app.repository.HotelBookingRepository;
import com.project.airbnb_app.repository.RoomRepository;
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
    private final RoomRepository roomRepository;
    private final AppUserRepository appUserRepository;
    private final GuestRepository guestRepository;

    @Override
    public BookingDto crateBooking(HotelBookingRequest hotelBookingRequest) {
        try {
            log.info("Crete booking request started with {}", hotelBookingRequest.toString());

            Hotel hotel = hotelService.getHotelById(hotelBookingRequest.getHotelId());
            log.info("Hotel found with the id: {}", hotel.getId());

            Room room = roomRepository.
                    findByIdAndHotelId(hotelBookingRequest.getRoomId(), hotelBookingRequest.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with the hotel id " + hotelBookingRequest.getHotelId() + " and room id " + hotelBookingRequest.getRoomId()));
            log.info("Room found with the id: {}", room.getId());

            User user = getAppUser();

            Set<Guest> guestSet = hotelBookingRequest
                    .getGuest()
                    .stream()
                    .map((guestDto) -> {
                        guestDto.setUser(user);
                        return modelMapper.map(guestDto, Guest.class);
                    })
                    .collect(Collectors.toSet());

            guestSet = guestRepository.saveAll(guestSet).stream().collect(Collectors.toSet());

            log.info("Guest dto converted into Guest. Total {} guest saved and available.", guestSet.size());

            log.info("Booking object preparing....");
            Booking booking = Booking
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

            Booking savedBooking = hotelBookingRepository.save(booking);
            log.info("Booking object prepared and saved with the id : {}", booking.getId());


            return modelMapper.map(savedBooking, BookingDto.class);
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
}
