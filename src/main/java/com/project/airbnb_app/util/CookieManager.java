package com.project.airbnb_app.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieManager {

    private static final String ENVIRONMENT_TO_ACCEPT_SECURE_COOKIE = "production";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${current.environment}")
    private String currentEnvironment;

    public void addRefreshTokenCookie(
            HttpServletResponse httpServletResponse,
            String refreshToken
    ) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        // Only for production.
        cookie.setSecure(currentEnvironment.equals(ENVIRONMENT_TO_ACCEPT_SECURE_COOKIE));
        // cookie.setMaxAge(); Optional property
        httpServletResponse.addCookie(cookie);
    }

    public String getRefreshTokenCookie(HttpServletRequest httpServletRequest) {
        return Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(() -> new IllegalStateException("Refresh token not found."));
    }
}
