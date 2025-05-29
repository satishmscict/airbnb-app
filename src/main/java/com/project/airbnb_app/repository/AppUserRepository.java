package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<User, Long> {
}
