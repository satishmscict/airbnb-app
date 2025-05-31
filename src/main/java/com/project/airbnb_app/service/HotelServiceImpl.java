package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelInfoDto;
import com.project.airbnb_app.dto.RoomDto;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.HotelRepository;
import com.project.airbnb_app.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final RoomInventoryService roomInventoryService;
    // TODO: Refactor to use RoomService and clean up related code.
    private final RoomRepository roomRepository;

    @Override
    public HotelDto activateHotel(Long hotelId) {
        log.info("Activate hotel with the id: {}.", hotelId);
        Hotel hotel = getHotelById(hotelId);
        hotel.setActive(true);
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel with id {} activated successfully.", hotelId);

        log.info("Create Inventory for Each Room.");
        for (Room room : hotel.getRooms()) {
            roomInventoryService.createInventory(hotelId, room.getId());
        }
        log.info("Inventory creation for each room has been successfully completed.");

        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Save hotel with the name: {}.", hotelDto.getName());
        Hotel toHotel = modelMapper.map(hotelDto, Hotel.class);
        toHotel.setActive(false);

        Hotel savedHotel = hotelRepository.save(toHotel);
        log.info("Hotel saved with the id {}.", savedHotel.getId());

        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    @Transactional
    public String deleteHotelById(Long hotelId) {
        log.info("Fetch hotel details with the id: {}.", hotelId);
        Hotel hotel = getHotelById(hotelId);

        log.info("Hotel found with the id {} and total {} rooms, now need to delete inventory of each room.", hotelId, hotel.getRooms().size());
        for (Room room : hotel.getRooms()) {
            roomInventoryService.deleteInventoryByHotelIdAndRoomId(hotel.getId(), room.getId());
            roomRepository.deleteById(room.getId());
        }
        log.info("Deleted inventory record for the total rooms {}.", hotel.getRooms().size());

        log.info("Child records of inventory deleted. Now delete the hotel entity with the id: {}.", hotelId);
        hotelRepository.deleteById(hotelId);
        log.info("Hotel with id {} deleted successfully.", hotelId);

        return "Hotel id " + hotelId + " successfully deleted.";
    }

    @Override
    public Page<HotelDto> findHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest) {
        return roomInventoryService.findHotelsByCityAndAvailability(hotelSearchRequest);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        log.info("Getting all hotels preparing.");

        List<Hotel> hotels = hotelRepository.findByActive(true);
        log.info("Get all hotels completed and total {} hotels found.", hotels.size());

        List<HotelDto> hotelDtoList = hotels
                .stream()
                .map((hotel) -> modelMapper.map(hotel, HotelDto.class))
                .toList();
        log.info("Converted all hotel into hotelDto list.");

        return hotelDtoList;
    }

    @Override
    public Hotel getHotelByIdAndIsActive(Long hotelId, Boolean isActive) {
        log.info("Find hotel with the id: {}", hotelId);
        if (!isHotelExistById(hotelId)) {
            throw new ResourceNotFoundException("Hotel not found with the id : " + hotelId);
        }

        Hotel hotel = hotelRepository
                .findByIdAndActive(hotelId, isActive)
                .orElseThrow(() -> new ResourceNotFoundException("The hotel exists but is not activated."));
        log.info("Hotel found with the id: {}", hotelId);

        return hotel;
    }

    @Override
    public HotelInfoDto getHotelDetailsInfo(Long hotelId) {
        Hotel hotel = getHotelByIdAndIsActive(hotelId, true);

        List<RoomDto> roomDtoList = hotel
                .getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return HotelInfoDto
                .builder()
                .hotel(modelMapper.map(hotel, HotelDto.class))
                .rooms(roomDtoList)
                .build();
    }

    @Override
    public HotelDto getHotelDtoByIdAndIsActive(Long hotelId) {
        log.info("Get hotel with the id {}.", hotelId);
        Hotel toHotel = getHotelByIdAndIsActive(hotelId, true);
        log.info("Hotel found with the id {} and name {}.", hotelId, toHotel.getName());

        return modelMapper.map(toHotel, HotelDto.class);
    }

    @Override
    public Boolean isHotelExistById(Long hotelId) {
        return hotelRepository.existsById(hotelId);
    }

    private Hotel getHotelById(Long hotelId) {
        return hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("The hotel not found with the id:  " + hotelId));
    }
}
