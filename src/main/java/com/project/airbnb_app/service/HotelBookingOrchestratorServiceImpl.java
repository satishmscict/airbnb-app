package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.entity.*;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.exception.UnAuthorizationException;
import com.project.airbnb_app.repository.HotelBookingRepository;
import com.project.airbnb_app.room_pricing_strategy.DynamicRoomPricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingOrchestratorServiceImpl implements HotelBookingOrchestratorService {

    private final AppUserDomainService appUserDomainService;
    private final DynamicRoomPricingService dynamicRoomPricingService;
    private final GuestDomainService guestDomainService;
    private final GuestService guestService;
    private final HotelBookingDomainService hotelBookingDomainService;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelDomainService hotelDomainService;
    private final ModelMapper modelMapper;
    private final PaymentGatewayService paymentGatewayService;
    private final RoomDomainService roomDomainService;
    private final RoomInventoryService roomInventoryService;



    @Override
    @Transactional
    public List<GuestDto> addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList) {
        log.debug("Adding guest to bookingId {} and total {} guests available.", bookingId, guestDtoList.size());

        HotelBooking hotelBooking = hotelBookingDomainService.findById(bookingId);
        hotelBookingDomainService.isBookingBelongsToCurrentUser(hotelBooking.getUser().getId());
        hotelBookingDomainService.validateBookingNotExpired(hotelBooking.getCreatedAt());
        hotelBookingDomainService.validateBookingStatusForAddGuests(hotelBooking);

        List<GuestDto> savedGuestDtoList = guestService.addGuests(guestDtoList);

        List<Long> guestIds = savedGuestDtoList.stream().map(GuestDto::getId).toList();
        List<Guest> guestList = guestDomainService.findGuestByIds(guestIds);

        hotelBooking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        hotelBooking.setGuest(guestList.stream().collect(Collectors.toSet()));
        hotelBookingRepository.save(hotelBooking);
        log.debug("Guests added and update status to GUESTS_ADDED");

        return guestList.stream()
                .map((element) -> modelMapper.map(element, GuestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.debug("Cancel the of bookingId {}.", bookingId);

        HotelBooking hotelBooking = hotelBookingDomainService.findById(bookingId);

        hotelBookingDomainService.isBookingBelongsToCurrentUser(hotelBooking.getUser().getId());

        if (hotelBooking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings eligible to cancel.");
        }

        hotelBooking.setBookingStatus(BookingStatus.CANCELLED);
        hotelBookingRepository.save(hotelBooking);

        roomInventoryService.findAndLockInventoryForModification(
                hotelBooking.getRoom().getId(),
                hotelBooking.getCheckInDate().toLocalDate(),
                hotelBooking.getCheckOutDate().toLocalDate(),
                hotelBooking.getRoomsCount()
        );

        roomInventoryService.releaseBookedRooms(
                hotelBooking.getRoom().getId(),
                hotelBooking.getCheckInDate().toLocalDate(),
                hotelBooking.getCheckOutDate().toLocalDate(),
                hotelBooking.getRoomsCount()
        );

        paymentGatewayService.processRefundAmount(hotelBooking);
    }

    @Transactional
    @Override
    public HotelBookingDto createHotelBooking(HotelBookingRequest hotelBookingRequest) {
        log.debug("Create booking request started with {}", hotelBookingRequest.toString());

        // Validating reserved rooms availability.
        long daysCount = ChronoUnit.DAYS.between(hotelBookingRequest.getCheckInDate(), hotelBookingRequest.getCheckOutDate()) + 1;
        List<RoomInventory> roomInventoryList = roomInventoryService.updateReservedRoomsCount(hotelBookingRequest);
        if (daysCount != roomInventoryList.size()) {
            throw new IllegalStateException("Rooms not available for " + daysCount + " days.");
        }

        Hotel hotel = hotelDomainService.getHotelByIdAndIsActivated(hotelBookingRequest.getHotelId());

        Room room = roomDomainService.getRoomById(
                hotelBookingRequest.getRoomId(),
                hotelBookingRequest.getHotelId()
        );

        User user = appUserDomainService.getCurrentUser();
        if (!Objects.equals(hotelBookingRequest.getUserId(), user.getId())) {
            throw new UnAuthorizationException("Booking does not belongs to the user id: " + hotelBookingRequest.getUserId());
        }

        BigDecimal priceForOneRoom = dynamicRoomPricingService.getTotalPrice(roomInventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(hotelBookingRequest.getBookedRoomsCount()));

        log.debug("Booking object preparing....");
        HotelBooking hotelBooking = HotelBooking.builder()
                .hotel(hotel)
                .room(room)
                .user(user)
                .bookingStatus(BookingStatus.RESERVED)
                .checkInDate(hotelBookingRequest.getCheckInDate())
                .checkOutDate(hotelBookingRequest.getCheckOutDate())
                .roomsCount(hotelBookingRequest.getBookedRoomsCount())
                .amount(totalPrice)
                .build();

        HotelBooking savedHotelBooking = hotelBookingRepository.save(hotelBooking);
        log.debug("Hotel booking object prepared and saved with the id : {}", savedHotelBooking.getId());

        return modelMapper.map(savedHotelBooking, HotelBookingDto.class);
    }

    @Override
    public String initiatePayment(Long bookingId) {
        log.debug("Start initiate payment.");
        HotelBooking hotelBooking = hotelBookingDomainService.findById(bookingId);

        User user = appUserDomainService.getCurrentUser();
        if (!Objects.equals(hotelBooking.getUser().getId(), user.getId())) {
            String errorMessage = String.format("Booking does not belongs to the user id: %s", hotelBooking.getUser().getId());
            log.error(errorMessage);
            throw new UnAuthorizationException(errorMessage);
        }

        hotelBookingDomainService.validateBookingNotExpired(hotelBooking.getCreatedAt());

        log.debug("Prepare the stripe payment request object and get the payment session url.");
        String paymentSessionUrl = paymentGatewayService.createCheckoutSession(hotelBooking);
        log.debug("Payment session created for booking id: {}", bookingId);

        log.debug("Update the payment status to PAYMENT_PENDING for the booking id: {}", bookingId);
        hotelBooking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        hotelBookingRepository.save(hotelBooking);
        log.debug("Successfully update the booking status to PAYMENT_PENDING for the booking id: {}", bookingId);

        return paymentSessionUrl;
    }
}
