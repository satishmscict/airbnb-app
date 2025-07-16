package com.project.airbnb_app.advice;

import com.project.airbnb_app.exception.ResourceNotFoundException;
import com.project.airbnb_app.exception.UnAuthorizationException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException authenticationException) {
        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(authenticationException.getMessage())
                .build();
        return toApiResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(accessDeniedException.getMessage())
                .build();
        return toApiResponseEntity(apiError);
    }

    @ExceptionHandler({
            JwtException.class,
            UnAuthorizationException.class
    })
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException jwtException) {
        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(jwtException.getMessage())
                .build();
        return toApiResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> errorList = methodArgumentNotValidException
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Input validation failed.")
                .errorList(errorList)
                .build();

        return toApiResponseEntity(apiError);
    }

    @ExceptionHandler({
            Exception.class,
            IllegalStateException.class,
            RuntimeException.class
    })
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception) {
        log.error("Error cause: {}", exception.toString());

        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();

        return toApiResponseEntity(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        ApiError apiError = ApiError
                .builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(resourceNotFoundException.getMessage())
                .build();
        return toApiResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> toApiResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getHttpStatus());
    }
}
