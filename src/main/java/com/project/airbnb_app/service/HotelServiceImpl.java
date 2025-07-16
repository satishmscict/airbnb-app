package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelAndRoomsDto;
import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelDomainService hotelDomainService;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.debug("Save hotel with the name: {}.", hotelDto.getName());
        Hotel toHotel = modelMapper.map(hotelDto, Hotel.class);
        toHotel.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        toHotel.setOwner(user);

        Hotel savedHotel = hotelRepository.save(toHotel);
        log.debug("Hotel saved with the id {}.", savedHotel.getId());

        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        log.debug("Getting all hotels preparing.");

        List<Hotel> hotels = hotelRepository.findByActive(true);
        log.debug("Get all hotels completed and total {} hotels found.", hotels.size());

        List<HotelDto> hotelDtoList = hotels
                .stream()
                .map((hotel) -> modelMapper.map(hotel, HotelDto.class))
                .toList();
        log.debug("Converted all hotel into hotelDto list.");

        return hotelDtoList;
    }

    @Override
    public HotelAndRoomsDto getHotelAndRoomsDetails(Long hotelId) {
        Hotel hotel = hotelDomainService.getHotelByIdAndIsActivated(hotelId);

        List<RoomDto> roomDtoList = hotel
                .getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return HotelAndRoomsDto
                .builder()
                .hotel(modelMapper.map(hotel, HotelDto.class))
                .rooms(roomDtoList)
                .build();
    }

    @Override
    public HotelDto getHotelByIdAndIsActive(Long hotelId) {
        log.debug("Get hotel with the id {}.", hotelId);
        Hotel toHotel = hotelDomainService.getHotelByIdAndIsActivated(hotelId);
        log.debug("Hotel found with the id {} and name {}.", hotelId, toHotel.getName());

        return modelMapper.map(toHotel, HotelDto.class);
    }

    @Override
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel == null) {
            throw new ResourceNotFoundException("Hotel not found with the id: " + hotelId);
        }

        hotelDomainService.validateHotelOwnership(hotel.getOwner().getId());
        hotelRepository.deleteById(hotelId);
    }

    @Override
    public void save(Hotel hotel) {
        hotelRepository.save(hotel);
    }
}
