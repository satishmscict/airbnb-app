package com.project.airbnb_app.controller;

import com.project.airbnb_app.advice.ApiResponse;
import com.project.airbnb_app.dto.LoginResponseDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.LoginRequestDto;
import com.project.airbnb_app.dto.request.SignupDto;
import com.project.airbnb_app.service.AuthenticationService;
import com.project.airbnb_app.util.CookieManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CookieManager cookieManager;

    @PostMapping
    ResponseEntity<UserDto> createUser(@Valid @RequestBody SignupDto signupDto) {
        UserDto userDto = authenticationService.createUser(signupDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDto> login(HttpServletResponse httpServletResponse, @Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authenticationService.signInUser(loginRequestDto);
        cookieManager.addRefreshTokenCookie(httpServletResponse, loginResponseDto.getRefreshToken());
        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/renew")
    ResponseEntity<ApiResponse<String>> renew(HttpServletRequest httpServletRequest) {
        String refreshToken = cookieManager.getRefreshTokenCookie(httpServletRequest);
        ApiResponse<String> apiResponse = new ApiResponse<>(authenticationService.renew(refreshToken));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
