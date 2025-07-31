package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.ProfileUpdateRequest;
import com.project.airbnb_app.entity.User;

public interface AppUserService {

    /**
     * Get the user profile.
     *
     * @param userId the user id.
     * @return the user profile details dto.
     */
    UserDto getUserProfile(Long userId);

    /**
     * Save user.
     *
     * @param user object.
     */
    void save(User user);

    /**
     * Update the user profile.
     *
     * @param userId                  the user id.
     * @param profileUpdateRequest the profile update details.
     */
    void updateUserProfile(Long userId, ProfileUpdateRequest profileUpdateRequest);
}
