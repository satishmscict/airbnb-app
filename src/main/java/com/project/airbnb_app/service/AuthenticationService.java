package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.LoginResponseDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.LoginRequestDto;
import com.project.airbnb_app.dto.request.SignupDto;

public interface AuthenticationService {

    /**
     * Create user.
     *
     * @param signupDto the signup dto
     * @return the userDto
     */
    UserDto createUser(SignupDto signupDto);

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
     * @param loginRequestDto login request object.
     * @return the login LoginResponseDto
     */
    LoginResponseDto signInUser(LoginRequestDto loginRequestDto);
}
