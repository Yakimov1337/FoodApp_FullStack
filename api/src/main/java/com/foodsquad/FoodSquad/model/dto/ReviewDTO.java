package com.foodsquad.FoodSquad.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ReviewDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique identifier of the review", example = "1")
    private Long id;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    @Schema(description = "Comment for the review", example = "Great food!", required = true)
    private String comment;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Rating for the review", example = "5", required = true)
    private int rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Date and time when the review was created", example = "2023-08-03T13:45:00")
    private LocalDateTime createdOn;

    @NotNull(message = "Menu Item ID is required")
    @Schema(description = "ID of the menu item being reviewed", example = "5", required = true)
    private Long menuItemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Email of the user who wrote the review", example = "user@example.com")
    private String userEmail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Image URL of the user who wrote the review", example = "https://example.com/image.jpg")
    private String imageUrl;

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

    public @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating must be at most 5") int getRating() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
