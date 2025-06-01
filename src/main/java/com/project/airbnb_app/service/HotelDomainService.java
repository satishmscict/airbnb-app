package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Don’t need to create an interface and a separate implementation class.
@RequiredArgsConstructor
@Component
@Slf4j
public class HotelDomainService {

    private final HotelRepository hotelRepository;

    Hotel getHotelById(Long hotelId) {
        log.debug("Get hotel with the id {}.", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + hotelId));
        log.debug("Hotel found with the id {} and name {}.", hotelId, hotel.getName());

        return hotel;
    }

    Hotel getHotelByIdAndIsActive(Long hotelId, Boolean isActive) {
        log.debug("Find hotel with the id: {} and isActive: {}", hotelId, isActive);
        if (!isHotelExistById(hotelId)) {
            throw new ResourceNotFoundException("Hotel not found with the id : " + hotelId);
        }

        Hotel hotel = hotelRepository
                .findByIdAndActive(hotelId, isActive)
                .orElseThrow(() -> new ResourceNotFoundException("The hotel exists but is not activated."));
        log.debug("Hotel found with the id: {}", hotelId);

        return hotel;
    }

    Boolean isHotelExistById(Long hotelId) {
        return hotelRepository.existsById(hotelId);
    }
}
