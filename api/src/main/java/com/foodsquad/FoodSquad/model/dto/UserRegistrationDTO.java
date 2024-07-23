package com.foodsquad.FoodSquad.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Size must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    @Size(min = 6, max = 20)
    private String confirmPassword;

    public @Email @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@Email @NotBlank String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 6, max = 20) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 20) String password) {
        this.password = password;
    }

    public @NotBlank @Size(min = 6, max = 20) String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank @Size(min = 6, max = 20) String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}