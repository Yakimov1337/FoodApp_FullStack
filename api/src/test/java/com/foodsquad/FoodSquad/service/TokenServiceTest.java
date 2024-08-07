package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.repository.TokenRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import com.foodsquad.FoodSquad.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsRefreshTokenValid() {
        // Arrange
        String username = "test@example.com";
        String refreshToken = "refreshToken";
        User user = new User();
        user.setEmail(username);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(tokenRepository.findByUserAndRefreshToken(user, refreshToken)).thenReturn(Optional.of(new Token()));

        // Act
        boolean result = tokenService.isRefreshTokenValid(username, refreshToken);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSaveTokens() {
        // Arrange
        String username = "test@example.com";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        User user = new User();
        user.setEmail(username);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        doNothing().when(tokenRepository).deleteByUser(user);

        // Act
        tokenService.saveTokens(username, accessToken, refreshToken);

        // Assert
        verify(tokenRepository, times(1)).deleteByUser(user);
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void testInvalidateTokens() {
        // Arrange
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        doNothing().when(tokenRepository).deleteByAccessToken(accessToken);
        doNothing().when(tokenRepository).deleteByRefreshToken(refreshToken);

        // Act
        tokenService.invalidateTokens(accessToken, refreshToken);

        // Assert
        verify(tokenRepository, times(1)).deleteByAccessToken(accessToken);
        verify(tokenRepository, times(1)).deleteByRefreshToken(refreshToken);
    }
}
