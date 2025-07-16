package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.LoginResponseDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.LoginRequestDto;
import com.project.airbnb_app.dto.request.SignupDto;

public interface AuthenticationService {

    UserDto createUser(SignupDto signupDto);

    String renew(String refreshToken);

    LoginResponseDto signInUser(LoginRequestDto loginRequestDto);
}
