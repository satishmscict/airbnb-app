package com.project.airbnb_app.security;

import com.project.airbnb_app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final String TOKEN_PREFIX = "Bearer ";

    @Value("${security.jwt.secret-key}")
    String jwtSecretKey;

    @Value("${security.jwt.access-token-expiration-time}")
    String accessTokenExpirationTime;

    @Value("${security.jwt.refresh-token-expiration-time}")
    String refreshTokenExpirationTime;

    private SecretKey getSecretKey() {
        byte[] secretKeyInBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretKeyInBytes);
    }

    @Override
    public String createAccessToken(User user) {
        log.trace("Start create access token for the user id: {}", user.getId());

        String accessToken = Jwts
                .builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("roles", user.getRoles().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSecretKey())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime)))
                .compact();
        log.trace("Access token generated for the user id: {}", user.getId());

        return accessToken;
    }

    @Override
    public String createRefreshToken(User user) {
        log.trace("Start create refresh token for the user id: {}", user.getId());
        String refreshToken = Jwts
                .builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime)))
                .signWith(getSecretKey())
                .compact();
        log.trace("Refresh token generated for the user id: {}", user.getId());

        return refreshToken;
    }

    @Override
    public Long getUserIdFromAccessToken(String token) {
        if (token.contains(TOKEN_PREFIX)) {
            log.trace("Token contains the prefix and replace {} with the empty ", TOKEN_PREFIX);
            token = token.split(TOKEN_PREFIX)[1];
        }

        log.trace("Get the user id using token.");
        Claims claims = Jwts
                .parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.trace("Extracted userid from the token and userId is: {}", claims.getSubject());
        return Long.valueOf(claims.getSubject());
    }
}
