package com.project.airbnb_app.security;

import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.service.AppUserDomainService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String TOKEN_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final AppUserDomainService appUserDomainService;

    @Qualifier("JwtAuthenticationHandlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {

        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.getUserIdFromAccessToken(authorizationHeader);

            // If user already available within a SecurityContextHolder, no need to check again.
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = appUserDomainService.getByIdOrNull(userId);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        /* principal */ user,
                        /* credentials */ null,
                        /* authorities */ user.getAuthorities()
                );
                // To get user IP address and perform rate limiting, DDOS attack
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            log.trace("JwtAuthenticationFilter process failed with the error : {}", exception.getLocalizedMessage());
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
