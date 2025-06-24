package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.Role;
import com.project.airbnb_app.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserDomainService {
    private final AppUserRepository appUserRepository;

    User getAppUser() {
        Set<Role> roleSet = EnumSet.of(Role.GUEST);

        User user = appUserRepository.findById(1L).orElse(null);
        if (user == null) {
            user = User
                    .builder()
                    .email("satish@gmail.com")
                    .name("Satish")
                    .roles(roleSet)
                    .password("sa@1234")
                    .build();
            user = appUserRepository.save(user);
        }

        return user;
    }
}
