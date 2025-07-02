package com.project.airbnb_app.repository;

import com.project.airbnb_app.dto.HotelMinimumPriceDto;
import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.HotelMinimumPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinimumPriceRepository extends JpaRepository<HotelMinimumPrice, Long> {

    @Query("""
            SELECT new com.project.airbnb_app.dto.HotelMinimumPriceDto(hmp.hotel.id, hmp.hotel.name, AVG(hmp.price))
            FROM HotelMinimumPrice hmp
            WHERE hmp.hotel.city = :city
                AND hmp.date between :startDate AND :endDate
                AND hmp.hotel.active = true
            Group by hmp.hotel.id, hmp.hotel.name
            """
    )
    Page<HotelMinimumPriceDto> findHotelsByCityWithCheapestPrice(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Optional<HotelMinimumPrice> findHotelByHotelAndDate(
            Hotel hotel,
            LocalDate date
    );
}
