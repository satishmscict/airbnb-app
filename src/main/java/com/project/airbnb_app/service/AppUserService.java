package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.User;

public interface AppUserService {

    /**
     * Save user.
     *
     * @param user object.
     */
    void save(User user);
}
