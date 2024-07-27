package com.foodsquad.FoodSquad.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserLoginDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(example = "admin@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Size must be between 6 and 20 characters")
    @Schema(example = "123123")
    private String password;

    public @Email @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@Email @NotBlank String email) {
        this.email = email;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
