package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.ReviewDTO;
import com.foodsquad.FoodSquad.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "6. Review Management", description = "Review Management API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Create a new review", description = "Create a new review with the provided details.")
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @Parameter(description = "Review details", required = true)
            @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO createdReview = reviewService.createReview(reviewDTO);
        return ResponseEntity.status(201).body(createdReview);
    }

    @Operation(summary = "Get reviews by user ID", description = "Retrieve a list of reviews for a specific user by their unique user ID.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUserId(
            @Parameter(description = "ID of the user whose reviews to retrieve", example = "1")
            @PathVariable String userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get reviews by menu item ID", description = "Retrieve a list of reviews for a specific menu item by its unique ID.")
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMenuItemId(
            @Parameter(description = "ID of the menu item whose reviews to retrieve", example = "1")
            @PathVariable Long menuItemId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByMenuItemId(menuItemId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Update a review by ID", description = "Update the details of an existing review by its unique ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @Parameter(description = "ID of the review to update", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated review details", required = true)
            @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "Get all reviews", description = "Retrieve a list of all reviews with pagination.")
    @GetMapping
    public List<ReviewDTO> getAllReviews(
            @Parameter(description = "Page number, starting from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "1000")
            @RequestParam(defaultValue = "1000") int size) {
        return reviewService.getAllReviews(page, size);
    }

    @Operation(summary = "Delete a review by ID", description = "Delete an existing review by its unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the review to delete", example = "1")
            @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
