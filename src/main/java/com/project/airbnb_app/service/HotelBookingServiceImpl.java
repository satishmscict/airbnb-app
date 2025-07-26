package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.GuestDto;
import com.project.airbnb_app.dto.HotelBookingDto;
import com.project.airbnb_app.dto.request.HotelBookingRequest;
import com.project.airbnb_app.entity.*;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.exception.UnAuthorizationException;
import com.project.airbnb_app.repository.HotelBookingRepository;
import com.project.airbnb_app.room_pricing_strategy.DynamicRoomPricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingServiceImpl implements HotelBookingService {

    private static final int BOOKING_EXPIRED_MINUTES = 10;
    private static final String STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";

    private final AppUserDomainService appUserDomainService;
    private final CheckoutService checkoutService;
    private final DynamicRoomPricingService dynamicRoomPricingService;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelDomainService hotelDomainService;
    private final GuestService guestService;
    private final ModelMapper modelMapper;
    private final RoomDomainService roomDomainService;
    private final RoomInventoryService roomInventoryService;

    @Value("${stripe.frontEndBaseUrl}")
    private String paymentGatewayRedirectBaseUrl;

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

        isBookingBelongsToCurrentUser(hotelBooking.getUser().getId());

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

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.debug("Cancel booking of bookingId {}.", bookingId);

        HotelBooking hotelBooking = hotelBookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel booking not found with the id: " + bookingId));

        isBookingBelongsToCurrentUser(hotelBooking.getUser().getId());

        if (hotelBooking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings eligible to cancel.");
        }

        hotelBooking.setBookingStatus(BookingStatus.CANCELLED);
        hotelBookingRepository.save(hotelBooking);

        roomInventoryService.findAndLockReserveInventory(
                hotelBooking.getRoom().getId(),
                hotelBooking.getCheckInDate().toLocalDate(),
                hotelBooking.getCheckOutDate().toLocalDate(),
                hotelBooking.getRoomsCount()
        );

        roomInventoryService.cancelBooking(
                hotelBooking.getRoom().getId(),
                hotelBooking.getCheckInDate().toLocalDate(),
                hotelBooking.getCheckOutDate().toLocalDate(),
                hotelBooking.getRoomsCount()
        );

        try {
            Session session = Session.retrieve(hotelBooking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    private void isBookingBelongsToCurrentUser(Long userId) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(userId, user.getId())) {
            throw new UnAuthorizationException("Booking does not belongs to the user id: " + user.getId());
        }
    }

    @Transactional
    @Override
    public void capturePaymentEvent(Event event) {
        if (event.getType().equals(STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED)) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            HotelBooking hotelBooking = hotelBookingRepository.findByPaymentSessionId(session.getId())
                    .orElseThrow(() -> {
                        String errorMessage = String.format("Booking not found for the session id: %s", session.getId());
                        log.error(errorMessage);
                        return new ResourceNotFoundException("Booking not found for the session id: " + session.getId());
                    });

            hotelBooking.setBookingStatus(BookingStatus.CONFIRMED);
            hotelBookingRepository.save(hotelBooking);

            // To avoid concurrent modification.
            roomInventoryService.findAndLockReserveInventory(
                    hotelBooking.getRoom().getId(),
                    hotelBooking.getCheckInDate().toLocalDate(),
                    hotelBooking.getCheckOutDate().toLocalDate(),
                    hotelBooking.getRoomsCount()
            );

            roomInventoryService.confirmBooking(
                    hotelBooking.getRoom().getId(),
                    hotelBooking.getCheckInDate().toLocalDate(),
                    hotelBooking.getCheckOutDate().toLocalDate(),
                    hotelBooking.getRoomsCount()
            );

            log.info("Successfully completed booking for the id: {}", hotelBooking.getId());
        } else {
            log.debug("Unhandled event type : {}", event.getType());
        }
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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public String getBookingStatus(Long bookingId) {
        HotelBooking hotelBooking = hotelBookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Booking not found with the id: %s", bookingId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });

        isBookingBelongsToCurrentUser(hotelBooking.getUser().getId());

        return hotelBooking.getBookingStatus().name();
    }

    @Override
    public String initiatePayment(Long bookingId) {
        log.debug("Start initiate payment.");
        HotelBooking hotelBooking = hotelBookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Booking not found with the id: %s", bookingId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(hotelBooking.getUser().getId(), user.getId())) {
            String errorMessage = String.format("Booking does not belongs to the user id: %s", hotelBooking.getUser().getId());
            log.error(errorMessage);
            throw new UnAuthorizationException(errorMessage);
        }

        if (isBookingExpired(hotelBooking.getCreatedAt())) {
            throw new IllegalStateException("Hotel booking has expired. Please initiate a new booking.");
        }

        log.debug("Prepare the stripe payment request object and get the payment session url.");
        String paymentSessionUrl = checkoutService.getCheckoutSession(
                hotelBooking,
                String.format("%s/payments/payment-success.html?user=%s&hotel=%s&room=%s&amount=%s&success=true",
                        paymentGatewayRedirectBaseUrl,
                        user.getName(),
                        hotelBooking.getHotel().getName(),
                        hotelBooking.getRoom().getType(),
                        hotelBooking.getAmount().toString()
                ),
                String.format("%s/payments/payment-cancel.html?user=%s&hotel=%s&room=%s&amount=%s",
                        paymentGatewayRedirectBaseUrl,
                        user.getName(),
                        hotelBooking.getHotel().getName(),
                        hotelBooking.getRoom().getType(),
                        hotelBooking.getAmount().toString()
                ));
        log.debug("Payment session created for booking id: {}", bookingId);

        log.debug("Update the payment status to PAYMENT_PENDING for the booking id: {}", bookingId);
        hotelBooking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        hotelBookingRepository.save(hotelBooking);
        log.debug("Successfully update the booking status to PAYMENT_PENDING for the booking id: {}", bookingId);

        return paymentSessionUrl;
    }

    private Boolean isBookingExpired(LocalDateTime bookingStartDate) {
        return bookingStartDate.plusMinutes(BOOKING_EXPIRED_MINUTES).isBefore(LocalDateTime.now());
    }
}
