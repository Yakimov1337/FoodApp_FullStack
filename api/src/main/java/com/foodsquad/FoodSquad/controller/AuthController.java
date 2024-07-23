package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "1. Authentication", description = "Authentication API")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserResponseDTO user = authService.registerUser(userRegistrationDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        UserResponseDTO user = authService.loginUser(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("imageUrl", user.getImageUrl());
        claims.put("phoneNumber", user.getPhoneNumber());

        String accessToken = jwtUtil.generateToken(claims, user.getEmail(), accessTokenExpiration);
        String refreshToken = jwtUtil.generateToken(claims, user.getEmail(), refreshTokenExpiration);

        authService.saveRefreshToken(user.getEmail(), refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) refreshTokenExpiration / 1000);
        response.addCookie(refreshTokenCookie);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        String username;
        try {
            username = jwtUtil.extractClaims(refreshToken).getSubject();
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token has expired: {}", refreshToken);
            throw e;
        } catch (JwtException e) {
            logger.error("Failed to extract claims from refresh token: {}", refreshToken);
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while extracting claims from refresh token: {}", refreshToken);
            throw e;
        }

        UserDetails userDetails = authService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            logger.warn("Refresh token validation failed for user: {}", username);
            throw new JwtException("Invalid refresh token");
        }

        if (!authService.isRefreshTokenValid(username, refreshToken)) {
            logger.warn("Refresh token is not present in the database for user: {}", username);
            throw new JwtException("Invalid refresh token");
        }

        Map<String, Object> claims = jwtUtil.extractClaims(refreshToken);

        String newAccessToken = jwtUtil.generateToken(claims, username, accessTokenExpiration);
        String newRefreshToken = jwtUtil.generateToken(claims, username, refreshTokenExpiration);

        // Invalidate the old refresh token
        authService.invalidateRefreshToken(refreshToken);

        // Save the new refresh token
        authService.saveRefreshToken(username, newRefreshToken);

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
}
