package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.TokenService;
import com.foodsquad.FoodSquad.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "1. Token Management", description = "API for managing JWT tokens")
@RestController
@RequestMapping("/api/token")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Operation(summary = "Refresh JWT token", description = "Refresh the JWT access token using the refresh token.")
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @Parameter(description = "Refresh token stored in cookie, no need to be valid input, browser will handle it!", example = "123", required = true)
            @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        String email;
        try {
            email = jwtUtil.extractClaims(refreshToken).getSubject();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from refresh token: {}", refreshToken);
            throw e;
        }

        UserDetails userDetails = authService.loadUserByUsername(email);


        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            logger.warn("Refresh token validation failed for user: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid refresh token"));
        }

        if (!tokenService.isRefreshTokenValid(email, refreshToken)) {
            logger.warn("Refresh token is not present in the database for user: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Refresh token not found"));
        }

        Map<String, Object> claims = jwtUtil.extractClaims(refreshToken);

        String newAccessToken = jwtUtil.generateToken(claims, email, accessTokenExpiration);
        String newRefreshToken = jwtUtil.generateToken(claims, email, refreshTokenExpiration);

        tokenService.invalidateTokens(null, refreshToken); // Invalidate only the refresh token here
        tokenService.saveTokens(email, newAccessToken, newRefreshToken); // Save new access and refresh tokens

        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) refreshTokenExpiration / 1000);
        response.addCookie(refreshTokenCookie);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);

        return ResponseEntity.ok(tokens);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestCookieException(MissingRequestCookieException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Refresh token is missing. Please log in again.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }
}
