package com.foodsquad.FoodSquad.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Map;


public class OrderDTO {
    //allows reading while returning the object only, TODO: create additional OrderCreateDTO
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    @NotNull(message = "Order ID is required")
    private String id;

    @NotNull(message = "Menu item quantities are required")
    @Schema(example = "{\"1\": 1, \"2\": 2}")
    private Map<Long, Integer> menuItemQuantities;

    @NotNull(message = "Status is required")
    @Schema(example = "PENDING")
    private OrderStatus status;

    @Positive(message = "Total cost must be positive")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalCost;

    @NotNull(message = "Creation date is required")
    private LocalDateTime createdOn;

    @Schema(defaultValue = "false")
    private Boolean paid = false;

    @Schema(example = "admin@example.com" )
    private String userEmail;

    // Public no-argument constructor
    public OrderDTO() {
    }

    // Constructor to create DTO from Order entity
    public OrderDTO(String id, String userEmail, Map<Long, Integer> menuItemQuantities, OrderStatus status, Double totalCost, LocalDateTime createdOn, Boolean paid) {
        this.id = id;
        this.userEmail = userEmail;
        this.menuItemQuantities = menuItemQuantities;
        this.status = status;
        this.totalCost = totalCost;
        this.createdOn = createdOn;
        this.paid = paid;
    }

    public String getId() {
        return id;
    }

    public void setId(String orderId) {
        this.id = orderId;
    }


    public Map<Long, Integer> getMenuItemQuantities() {
        return menuItemQuantities;
    }

    public void setMenuItemQuantities(Map<Long, Integer> menuItemQuantities) {
        this.menuItemQuantities = menuItemQuantities;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status.name();
    }

    public void setStatus(@NotNull(message = "Status is required") OrderStatus status) {
        this.status = status;
    }

    public @Positive(message = "Total cost must be positive") Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(@Positive(message = "Total cost must be positive") Double totalCost) {
        this.totalCost = totalCost;
    }

    public @NotNull(message = "Creation date is required") LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(@NotNull(message = "Creation date is required") LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
