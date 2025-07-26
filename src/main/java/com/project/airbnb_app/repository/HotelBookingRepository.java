package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {

    Optional<HotelBooking> findByPaymentSessionId(String id);
}
