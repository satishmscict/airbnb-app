package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto activateHotel(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + hotelId));
        hotel.setActive(true);
        Hotel savedHotel = hotelRepository.save(hotel);

        //TODO:  create inventory when activate the hotel.
        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Save hotel with the name: {}", hotelDto.getName());

        Hotel toHotel = modelMapper.map(hotelDto, Hotel.class);
        toHotel.setActive(false);
        Hotel savedHotel = hotelRepository.save(toHotel);
        log.info("Hotel saved with the id {}", savedHotel.getId());

        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel not found with the id: " + hotelId);
        }

        hotelRepository.deleteById(hotelId);
        //TODO:: delete inventory details.
    }

    @Override
    public List<HotelDto> getAllHotels() {
        log.info("Getting all hotels preparing");

        List<Hotel> hotels = hotelRepository.findByActive(true);
        log.info("Get all hotels completed and total {} hotels found.", hotels.size());

        List<HotelDto> hotelDtoList = hotels
                .stream()
                .map((hotel) -> modelMapper.map(hotel, HotelDto.class))
                .toList();
        log.info("Converted all hotel into hotelDto list");

        return hotelDtoList;
    }

    @Override
    public HotelDto getHotelById(Long hotelId) {
        log.info("Get hotel with the id {}", hotelId);

        Hotel toHotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the id: " + hotelId));

        log.info("Hotel found with the id {} and name {}", hotelId, toHotel.getName());
        return modelMapper.map(toHotel, HotelDto.class);
    }
}
