package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.exception.UnAuthorizationException;
import com.project.airbnb_app.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
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

        validateHotelOwnership(hotel.getOwner().getId());

        return hotel;
    }

    Hotel getHotelByIdAndIsActivated(Long hotelId) {
        log.debug("Find hotel with the id: {} and isActive: {}", hotelId, true);
        if (!isHotelExistById(hotelId)) {
            throw new ResourceNotFoundException("Hotel not found with the id : " + hotelId);
        }

        Hotel hotel = hotelRepository
                .findByIdAndActive(hotelId, true)
                .orElseThrow(() -> new ResourceNotFoundException("The hotel exists but is not activated."));
        log.debug("Hotel found with the id: {}", hotelId);

        return hotel;
    }

    Page<Hotel> getHotels(int pageSize, int batchSize) {
        return hotelRepository.findAll(PageRequest.of(pageSize, batchSize));
    }

    Boolean isHotelExistById(Long hotelId) {
        return hotelRepository.existsById(hotelId);
    }

    public void validateHotelOwnership(Long ownerId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getId().equals(ownerId)) {
            throw new UnAuthorizationException("User does not own this hotel.");
        }
    }
}
