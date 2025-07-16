package com.project.airbnb_app.repository;

import com.project.airbnb_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
