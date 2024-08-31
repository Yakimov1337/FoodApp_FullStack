package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.TokenService;
import com.foodsquad.FoodSquad.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "2. Authentication", description = "Authentication API")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

    @Operation(summary = "User registration", description = "Register a new user with the provided registration details.")
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserResponseDTO user = authService.registerUser(userRegistrationDTO);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "User login", description = "Authenticate a user with the provided login credentials.")
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> loginUser(
            @Parameter(description = "User login details", required = true)
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletResponse response) {
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

        tokenService.saveTokens(user.getEmail(), accessToken, refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);

        return ResponseEntity.ok(tokens);
    }

    @Operation(summary = "Logout user", description = "Invalidate the refresh token and logout the user.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(
            @Parameter(description = "Access token stored in header", required = true)
            @RequestHeader("Authorization") String accessTokenHeader,
            @Parameter(description = "Refresh token stored in cookie", required = true)
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");

        String email;
        try {
            email = jwtUtil.extractClaims(refreshToken).getSubject();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from refresh token: {}", refreshToken);
            throw e;
        }

        tokenService.invalidateTokens(accessToken, refreshToken);

        // Clear refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Successfully logged out");

        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "Get current user", description = "Get the details of the currently authenticated user.")
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String accessTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        String email;
        try {
            email = jwtUtil.extractClaims(accessToken).getSubject();
        } catch (ExpiredJwtException e) {
            logger.error("Access token is expired: {}", accessToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Access token is expired"));
        } catch (JwtException e) {
            logger.error("Failed to extract claims from access token: {}", accessToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Failed to extract claims from access token"));
        }

        User user = authService.loadUserEntityByUsername(email);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok(userResponseDTO);
    }

}
