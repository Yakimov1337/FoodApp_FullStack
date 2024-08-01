package com.foodsquad.FoodSquad.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ReviewDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdOn;

    @NotNull(message = "Menu Item ID is required")
    private Long menuItemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Comment cannot be blank") @Size(max = 1000, message = "Comment cannot exceed 1000 characters") String getComment() {
        return comment;
    }

    public void setComment(@NotBlank(message = "Comment cannot be blank") @Size(max = 1000, message = "Comment cannot exceed 1000 characters") String comment) {
        this.comment = comment;
    }

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    public int getRating() {
        return rating;
    }

    public void setRating(@NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating must be at most 5") int rating) {
        this.rating = rating;
    }

    public @NotNull(message = "Menu Item ID is required") Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(@NotNull(message = "Menu Item ID is required") Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
