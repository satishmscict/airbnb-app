package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.exception.UnAuthorizationException;
import com.project.airbnb_app.repository.HotelBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelBookingServiceImpl implements HotelBookingService {

    private final AppUserDomainService appUserDomainService;
    private final HotelBookingRepository hotelBookingRepository;

    private void isBookingBelongsToCurrentUser(Long userId) {
        User user = appUserDomainService.getCurrentUser();
        if (!Objects.equals(userId, user.getId())) {
            throw new UnAuthorizationException("Booking does not belongs to the user id: " + user.getId());
        }
    }

    @Override
    public HotelBooking findByPaymentSessionId(String paymentSessionId) {
        return hotelBookingRepository.findByPaymentSessionId(paymentSessionId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Booking not found for the session id: %s", paymentSessionId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
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
    public void saveBooking(HotelBooking hotelBooking) {
        hotelBookingRepository.save(hotelBooking);
    }
}
