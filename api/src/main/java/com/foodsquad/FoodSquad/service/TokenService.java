package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.repository.TokenRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import com.foodsquad.FoodSquad.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Token> token = tokenRepository.findByUserAndToken(user, refreshToken);
        return token.isPresent();
    }

    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //End all other sessions
        tokenRepository.deleteByUser(user);

        Token token = new Token();
        token.setToken(refreshToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        token.setUser(user);

        tokenRepository.save(token);
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        tokenRepository.deleteByToken(refreshToken);
    }
}
