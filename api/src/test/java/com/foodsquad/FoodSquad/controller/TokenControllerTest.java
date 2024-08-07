package com.foodsquad.FoodSquad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.TokenService;
import com.foodsquad.FoodSquad.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TokenControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenController tokenController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tokenController).build();
    }

    @Test
    void testRefreshToken() throws Exception {
        String refreshToken = "refreshToken";
        String email = "user@example.com";

        Claims claims = io.jsonwebtoken.Jwts.claims();
        claims.setSubject(email);

        when(jwtUtil.extractClaims(anyString())).thenReturn(claims);
        when(authService.loadUserByUsername(anyString())).thenReturn(new org.springframework.security.core.userdetails.User(email, "password", new ArrayList<>()));
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);
        when(tokenService.isRefreshTokenValid(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(Claims.class), anyString(), anyLong())).thenReturn("newAccessToken", "newRefreshToken");

        MockHttpServletResponse response = mockMvc.perform(post("/api/token/refresh-token")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Map<String, String> responseMap = new ObjectMapper().readValue(response.getContentAsString(), Map.class);
        assertEquals("newAccessToken", responseMap.get("accessToken"));
    }

    @Test
    void testRefreshTokenWithInvalidToken() throws Exception {
        String refreshToken = "invalidRefreshToken";
        String email = "user@example.com";

        Claims claims = io.jsonwebtoken.Jwts.claims();
        claims.setSubject(email);

        when(jwtUtil.extractClaims(anyString())).thenReturn(claims);
        when(authService.loadUserByUsername(anyString())).thenReturn(new org.springframework.security.core.userdetails.User(email, "password", new ArrayList<>()));
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(false);
        when(tokenService.isRefreshTokenValid(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/token/refresh-token")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isForbidden());
    }
}
