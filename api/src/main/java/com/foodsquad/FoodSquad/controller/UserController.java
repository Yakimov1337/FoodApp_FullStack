package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.dto.UserUpdateDTO;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "3. User Management", description = "User Management API")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Operation(summary = "Create a new user(Admin panel usage)", description = "Create a new user with the provided registration details.")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO user = authService.registerUser(userRegistrationDTO);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users with pagination.")
    @GetMapping
    public List<UserResponseDTO> getAllUsers(
            @Parameter(description = "Page number, starting from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @Operation(summary = "Get a user by ID", description = "Retrieve a user by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1")
            @PathVariable String id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Update a user by ID", description = "Update the details of an existing user by their unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", example = "1")
            @PathVariable String id,

            @Parameter(description = "Updated user details", required = true)
            @Valid @RequestBody UserUpdateDTO updateUserDTO) {
        return userService.updateUser(id, updateUserDTO);
    }

    @Operation(summary = "Delete a user by ID", description = "Delete an existing user by their unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "1")
            @PathVariable String id) {
        return userService.deleteUser(id);
    }
}
