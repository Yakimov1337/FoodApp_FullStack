package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.repository.TokenRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import com.foodsquad.FoodSquad.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Token> token = tokenRepository.findByUserAndRefreshToken(user, refreshToken);
        return token.isPresent();
    }

    @Transactional
    public void saveTokens(String username, String accessToken, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // THIS WILL END ALL OTHER LOGGED USERS SESSIONS!!!
        tokenRepository.deleteByUser(user);

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessTokenExpiryDate(LocalDateTime.now().plus(Duration.ofMillis(accessTokenExpiration)));
        token.setRefreshTokenExpiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));
        token.setUser(user);

        tokenRepository.save(token);
    }

    @Transactional
    public void invalidateTokens(String accessToken, String refreshToken) {
        if (accessToken != null) {
            tokenRepository.deleteByAccessToken(accessToken);
        }
        if (refreshToken != null) {
            tokenRepository.deleteByRefreshToken(refreshToken);
        }
    }
}
