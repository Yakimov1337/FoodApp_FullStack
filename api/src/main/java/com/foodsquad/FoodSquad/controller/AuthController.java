package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserResponseDTO user = authService.registerUser(userRegistrationDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserResponseDTO user = authService.loginUser(userLoginDTO);
        return ResponseEntity.ok(user);
    }
}