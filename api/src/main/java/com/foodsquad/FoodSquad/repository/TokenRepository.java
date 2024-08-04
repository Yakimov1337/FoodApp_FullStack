package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserAndAccessToken(User user, String accessToken);
    Optional<Token> findByUserAndRefreshToken(User user, String refreshToken);
    void deleteByUser(User user);
    void deleteByAccessToken(String accessToken);
    void deleteByRefreshToken(String refreshToken);
    boolean existsByAccessToken(String accessToken);
}
