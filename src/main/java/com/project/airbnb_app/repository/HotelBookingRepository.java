package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {

    List<HotelBooking> findAllByUser(User user);

    List<HotelBooking> findAllByHotelAndBookingStatusAndCreatedAtBetween(
            Hotel hotel,
            BookingStatus bookingStatus,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<HotelBooking> findAllByHotel(Hotel hotel);

    Optional<HotelBooking> findByPaymentSessionId(String id);
}
