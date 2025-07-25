package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserDomainService {
    private final AppUserRepository appUserRepository;

    public User findByEmailOrNull(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    public User findByIdOrNull(Long userId) {
        return appUserRepository.findById(userId).orElse(null);
    }

    public User findByIdOrThrow(Long userId) {
        return appUserRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found the given id: " + userId));
    }
}
