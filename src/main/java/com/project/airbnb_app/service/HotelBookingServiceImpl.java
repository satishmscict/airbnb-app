package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.entity.*;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.entity.enums.Role;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.AppUserRepository;
import com.project.airbnb_app.repository.HotelBookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingServiceImpl implements HotelBookingService {

    private static final int BOOKING_EXPIRED_MINUTES = 10;

    // TODO: Refactor to use AppUserService and clean up related code.
    private final AppUserRepository appUserRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelDomainService hotelDomainService;
    private final GuestService guestService;
    private final ModelMapper modelMapper;
    private final RoomInventoryService roomInventoryService;
    private final RoomService roomService;

    private static void validateBookingStatus(HotelBooking hotelBooking) {
        switch (hotelBooking.getBookingStatus()) {
            case GUESTS_ADDED:
                throw new IllegalStateException("Hey, you have already added a guest for this booking.");
            case RESERVED:
                break;
            default:
                throw new IllegalStateException("Hotel booking status is not RESERVED.");
        }
    }

    @Override
    @Transactional
    public List<GuestDto> addGuestsToBooking(Long bookingId, List<GuestDto> guestList) {
        log.debug("Adding guest to bookingId {} and total {} guests available.", bookingId, guestList.size());

        HotelBooking hotelBooking = hotelBookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel booking not found with the id: " + bookingId));

        if (isBookingExpired(hotelBooking.getCreatedAt())) {
            throw new IllegalStateException("Hotel booking has expired. Please initiate a new booking.");
        }

        validateBookingStatus(hotelBooking);

        List<GuestDto> guestDtoList = guestService.addGuests(hotelBooking.getUser(), guestList);
        hotelBooking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        hotelBookingRepository.save(hotelBooking);
        log.debug("Guests added and update status to GUESTS_ADDED");

        return guestDtoList;
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

    @Transactional
    @Override
    public HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest) {
        log.debug("Create booking request started with {}", hotelBookingRequest.toString());

            //Reserved the rooms
            long daysCount = ChronoUnit.DAYS.between(hotelBookingRequest.getCheckInDate(), hotelBookingRequest.getCheckOutDate()) + 1;
            List<RoomInventory> roomInventoryList = roomInventoryService.updateReservedRoomsCount(hotelBookingRequest);
            if (daysCount != roomInventoryList.size()) {
                throw new IllegalStateException("Rooms not available for " + daysCount + " days.");
            }

        Hotel hotel = hotelDomainService.getHotelByIdAndIsActive(
                    hotelBookingRequest.getHotelId(),
                    true
            );

            Room room = roomService.getRoomByHotelIdAndRoomId(
                    hotelBookingRequest.getRoomId(),
                    hotelBookingRequest.getHotelId()
            );

            User user = getAppUser();

        log.debug("Booking object preparing....");
            HotelBooking hotelBooking = HotelBooking.builder()
                    .hotel(hotel)
                    .room(room)
                    .user(user)
                    .bookingStatus(BookingStatus.RESERVED)
                    .checkInDate(hotelBookingRequest.getCheckInDate())
                    .checkOutDate(hotelBookingRequest.getCheckOutDate())
                    .roomsCount(hotelBookingRequest.getBookedRoomsCount())
                    .amount(BigDecimal.TEN)
                    .build();

            HotelBooking savedHotelBooking = hotelBookingRepository.save(hotelBooking);
        log.debug("Hotel booking object prepared and saved with the id : {}", savedHotelBooking.getId());

            return modelMapper.map(savedHotelBooking, HotelBookingDto.class);
    }

    private Boolean isBookingExpired(LocalDateTime bookingStartDate) {
        return bookingStartDate.plusMinutes(BOOKING_EXPIRED_MINUTES).isBefore(LocalDateTime.now());
    }
}
