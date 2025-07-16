package com.project.airbnb_app.security;

import com.project.airbnb_app.entity.User;

public interface JwtService {

    String createAccessToken(User user);

    String createRefreshToken(User user);

    Long getUserIdFromAccessToken(String token);
}
