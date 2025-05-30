package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

}
