package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.LoginDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.LoginRequest;
import com.project.airbnb_app.dto.request.SignupRequest;

public interface AuthenticationService {

    /**
     * Create user.
     *
     * @param signupRequest the signup dto
     * @return the userDto
     */
    UserDto createUser(SignupRequest signupRequest);

    /**
     * Renew refresh token.
     *
     * @param refreshToken the refresh token.
     * @return the new accessToken.
     */
    String renew(String refreshToken);

    /**
     * Perform authentication based on LoginRequestDto.
     *
     * @param loginRequest login request object.
     * @return the login LoginResponseDto
     */
    LoginDto signInUser(LoginRequest loginRequest);
}
