package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.ProfileUpdateRequest;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl implements AppUserService, UserDetailsService {
    private final ModelMapper modelMapper;

    private final AppUserRepository appUserRepository;
    private final AppUserDomainService appUserDomainService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserDomainService.getByEmailOrNull(username);
    }

    @Override
    public UserDto getUserProfile(Long userId) {
        User user = appUserDomainService.getCurrentUser();

        validateUser(userId);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void save(User user) {
        appUserRepository.save(user);
    }

    @Override
    public void updateUserProfile(Long userId, ProfileUpdateRequest profileUpdateRequest) {
        log.debug("Start update user profile with the userID : {}", userId);
        User user = appUserDomainService.getCurrentUser();

        validateUser(userId);

        if (profileUpdateRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(profileUpdateRequest.getDateOfBirth());
        }
        if (profileUpdateRequest.getGender() != null) {
            user.setGender(profileUpdateRequest.getGender());
        }
        if (profileUpdateRequest.getName() != null) {
            user.setName(profileUpdateRequest.getName());
        }

        appUserRepository.save(user);

        log.debug("User profile updated successfully.");
    }

    private void validateUser(Long userId) {
        User user = appUserDomainService.getCurrentUser();
        if (!user.getId().equals(userId)) {
            String errorMessage = "User not belongs to the current user.";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }
    }
}
