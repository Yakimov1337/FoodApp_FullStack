package com.foodsquad.FoodSquad.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, String> errors = new HashMap<>();
        if (request.getAttribute("expired") != null) {
            errors.put("error", "Token has expired: " + request.getAttribute("expired"));
        } else if (request.getAttribute("invalid") != null) {
            errors.put("error", "Invalid token: " + request.getAttribute("invalid"));
        } else {
            errors.put("error", "Authentication failed: " + authException.getMessage());
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(errors));
    }
}
