package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.exception.UnAuthorizationException;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelBookingDomainService {

    private static final int BOOKING_EXPIRED_MINUTES = 10;

    private final AppUserDomainService appUserDomainService;
    private final HotelBookingRepository hotelBookingRepository;

    public void checkBookingStatusIsConfirmed(HotelBooking hotelBooking) {
        if (hotelBooking.getBookingStatus() != BookingStatus.CONFIRMED) {
            String errorMessage = "Only confirmed bookings eligible to cancel.";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public void checkBookingStatusIsReserved(HotelBooking hotelBooking) {
        BookingStatus status = hotelBooking.getBookingStatus();

        if (status == BookingStatus.RESERVED) {
            return;
        }

        String errorMessage = (status == BookingStatus.GUESTS_ADDED)
                ? "You have already added a guest for this booking."
                : "Hotel booking status is not RESERVED.";

        log.error(errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    public void checkBookingTimeIsNotExpired(LocalDateTime bookingStartDate) {
        if (bookingStartDate.plusMinutes(BOOKING_EXPIRED_MINUTES).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Hotel booking has expired. Please initiate a new booking.");
        }
    }

    public HotelBooking findByPaymentSessionId(String paymentSessionId) {
        return hotelBookingRepository.findByPaymentSessionId(paymentSessionId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Booking not found for the session id: %s", paymentSessionId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

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

    public void isBookingBelongsToCurrentUser(Long userId) {
        User user = appUserDomainService.getCurrentUser();
        if (!Objects.equals(userId, user.getId())) {
            String errorMessage = String.format("Booking does not belongs to the user id: %s", userId);
            log.error(errorMessage);
            throw new UnAuthorizationException(errorMessage);
        }
    }

    public HotelBooking findById(Long bookingId) {
        return hotelBookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Booking not found with the id: %s", bookingId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }
}
