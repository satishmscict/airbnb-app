package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.Hotel;
import com.project.airbnb_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByActive(Boolean isActive);

    Optional<Hotel> findByIdAndActive(Long id, Boolean active);

    List<Hotel> findByOwnerAndActive(User user, boolean isActive);
}
