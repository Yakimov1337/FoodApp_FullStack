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
    @NotNull(message = "Order ID is required")
    private UUID id;

    private UserResponseDTO user;

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

    // Default constructor
    public OrderDTO() {
        this.status = OrderStatus.PENDING;
        this.totalCost = 1;
        this.paid = false;
    }

    // Constructor to create DTO from Order entity
    public OrderDTO(Order order) {
        this.id = order.getId();
        this.user = new UserResponseDTO(order.getUser());
        this.menuItemIds = order.getMenuItems().stream().map(MenuItem::getId).collect(Collectors.toList());
        this.status = order.getStatus();
        this.totalCost = order.getTotalCost();
        this.createdOn = order.getCreatedOn();
        this.paid = order.getPaid();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID orderId) {
        this.id = orderId;
    }


    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public @NotNull(message = "Menu item IDs are required") List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(@NotNull(message = "Menu item IDs are required") List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public @NotNull(message = "Status is required") OrderStatus getStatus() {
        return status;
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
}
