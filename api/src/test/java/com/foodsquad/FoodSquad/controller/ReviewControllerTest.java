package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.ReviewDTO;
import com.foodsquad.FoodSquad.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewService reviewService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void testCreateReview() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO();
        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Great food!\",\"rating\":5,\"menuItemId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetReviewsByUserId() throws Exception {
        List<ReviewDTO> reviews = List.of(new ReviewDTO());
        when(reviewService.getReviewsByUserId(anyString())).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetReviewsByMenuItemId() throws Exception {
        List<ReviewDTO> reviews = List.of(new ReviewDTO());
        when(reviewService.getReviewsByMenuItemId(anyLong())).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/menu-item/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAllReviews() throws Exception {
        List<ReviewDTO> reviews = List.of(new ReviewDTO());
        when(reviewService.getAllReviews(anyInt(), anyInt())).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateReview() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO();
        when(reviewService.updateReview(anyLong(), any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Updated comment\",\"rating\":4,\"menuItemId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());
    }
}
