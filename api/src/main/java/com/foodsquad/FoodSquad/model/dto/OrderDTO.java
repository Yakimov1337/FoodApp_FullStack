package com.foodsquad.FoodSquad.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderDTO {
    //allows reading while returning the object only, TODO: create additional OrderCreateDTO
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    @NotNull(message = "Order ID is required")
    private String id;

    @NotNull(message = "Menu item IDs are required")
    private List<Long> menuItemIds;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @Positive(message = "Total cost must be positive")
    @Schema(defaultValue = "1")
    private Integer totalCost = 1;

    @NotNull(message = "Creation date is required")
    private LocalDateTime createdOn;

    @Schema(defaultValue = "false")
    private Boolean paid = false;

    private String userEmail;

    // Public no-argument constructor
    public OrderDTO() {
    }

    // Constructor to create DTO from Order entity
    public OrderDTO(String id, String userEmail, List<Long> menuItemIds, OrderStatus status, Integer totalCost, LocalDateTime createdOn, Boolean paid) {
        this.id = id;
        this.userEmail = userEmail;
        this.menuItemIds = menuItemIds;
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


    public @NotNull(message = "Menu item IDs are required") List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(@NotNull(message = "Menu item IDs are required") List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status.name().toLowerCase();
    }

    public void setStatus(@NotNull(message = "Status is required") OrderStatus status) {
        this.status = status;
    }

    public @Positive(message = "Total cost must be positive") Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(@Positive(message = "Total cost must be positive") Integer totalCost) {
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
