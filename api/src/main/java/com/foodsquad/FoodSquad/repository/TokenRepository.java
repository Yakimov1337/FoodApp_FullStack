package com.foodsquad.FoodSquad.repository;

import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserAndToken(User user, String token);
    void deleteByUser(User user);
    void deleteByToken(String token);
}
