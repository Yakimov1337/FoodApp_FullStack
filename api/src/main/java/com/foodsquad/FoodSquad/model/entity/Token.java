package com.foodsquad.FoodSquad.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "access_token_expiry_date", nullable = false)
    private LocalDateTime accessTokenExpiryDate;

    @Column(name = "refresh_token_expiry_date", nullable = false)
    private LocalDateTime refreshTokenExpiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getAccessTokenExpiryDate() {
        return accessTokenExpiryDate;
    }

    public void setAccessTokenExpiryDate(LocalDateTime accessTokenExpiryDate) {
        this.accessTokenExpiryDate = accessTokenExpiryDate;
    }

    public LocalDateTime getRefreshTokenExpiryDate() {
        return refreshTokenExpiryDate;
    }

    public void setRefreshTokenExpiryDate(LocalDateTime refreshTokenExpiryDate) {
        this.refreshTokenExpiryDate = refreshTokenExpiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
