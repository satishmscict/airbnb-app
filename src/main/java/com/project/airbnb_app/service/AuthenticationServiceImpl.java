package com.project.airbnb_app.service;

import com.project.airbnb_app.dto.LoginResponseDto;
import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.dto.request.LoginRequestDto;
import com.project.airbnb_app.dto.request.SignupDto;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.Role;
import com.project.airbnb_app.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AppUserDomainService appUserDomainService;
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(SignupDto signupDto) {
        log.trace("Check user already exist with the email: {}", signupDto.getEmail());
        User user = appUserDomainService.findByEmailOrNull(signupDto.getEmail());

        if (user != null) {
            log.trace("User already exist with the email : {}", signupDto.getEmail());
            throw new IllegalStateException("User already exist with the email id: " + signupDto.getEmail());
        }

        log.trace("User not exist and create new user with email: {}", signupDto.getEmail());
        String encryptPassword = bCryptPasswordEncoder.encode(signupDto.getPassword());
        user = User.builder()
                .name(signupDto.getName())
                .email(signupDto.getEmail())
                .password(encryptPassword)
                .roles(Set.of(Role.GUEST))
                .build();
        appUserService.save(user);

        User newUser = appUserDomainService.findByEmailOrNull(user.getEmail());

        return modelMapper.map(newUser, UserDto.class);
    }

    @Override
    public String renew(String refreshToken) {
        Long userId = jwtService.getUserIdFromAccessToken(refreshToken);
        User user = appUserDomainService.findByIdOrThrow(userId);

        return jwtService.createAccessToken(user);
    }

    @Override
    public LoginResponseDto signInUser(LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        return LoginResponseDto.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
