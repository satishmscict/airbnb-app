package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.HotelDto;
import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.dto.request.HotelMiniumPriceRequest;
import com.project.airbnb_app.dto.request.HotelSearchRequest;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.Room;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelOrchestratorServiceImpl implements HotelOrchestratorService {

    private final AppUserDomainService appUserDomainService;
    private final HotelDomainService hotelDomainService;
    private final HotelMinimPriceService hotelMinimPriceService;
    private final HotelService hotelService;
    private final ModelMapper modelMapper;
    private final RoomInventoryService roomInventoryService;
    private final RoomService roomService;

    @Override
    public HotelDto activateHotel(Long hotelId) {
        log.debug("Activate hotel with the id: {}.", hotelId);
        Hotel hotel = hotelDomainService.getHotelById(hotelId);
        hotel.setActive(true);
        hotelService.save(hotel);
        log.debug("Hotel with id {} activated successfully.", hotelId);

        log.debug("Create Inventory for Each Room. Total {} rooms available.", hotel.getRooms().size());
        for (Room room : hotel.getRooms()) {
            roomInventoryService.createInventory(hotelId, room.getId());
        }
        log.debug("Inventory creation for each room has been successfully completed.");

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public String deleteHotelWithDependencies(Long hotelId) {
        log.debug("Fetch hotel details with the id: {}.", hotelId);
        Hotel hotel = hotelDomainService.getHotelById(hotelId);
        hotelDomainService.validateHotelOwnership(hotel.getOwner().getId());

        log.debug("Hotel found with the id {} and total {} rooms, now need to delete inventory of each room.", hotelId, hotel.getRooms().size());
        for (Room room : hotel.getRooms()) {
            roomInventoryService.deleteInventoryByHotelIdAndRoomId(hotel.getId(), room.getId());
            roomService.deleteRoom(hotelId, room.getId());
        }
        log.debug("Deleted inventory record for the total rooms {}.", hotel.getRooms().size());

        log.debug("Child records of inventory deleted. Now delete the hotel entity with the id: {}.", hotelId);
        hotelService.deleteHotel(hotelId);
        log.debug("Hotel with id {} deleted successfully.", hotelId);

        return "Hotel id " + hotelId + " successfully deleted.";
    }

    @Override
    public Page<HotelDto> searchHotelsByCityAndAvailability(HotelSearchRequest hotelSearchRequest) {
        return roomInventoryService.searchHotelsByCityAndAvailability(hotelSearchRequest);
    }

    @Override
    public Page<HotelMinimumPriceDto> searchHotelsByCityWithMiniumPrice(HotelMiniumPriceRequest hotelMiniumPriceRequest) {
        return hotelMinimPriceService.searchHotelsByCityWithMinimumPrice(hotelMiniumPriceRequest);
    }
}
