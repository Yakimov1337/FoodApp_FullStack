package com.foodsquad.FoodSquad.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDTO {
    @NotNull
    private Integer totalCost;

    @NotNull
    private String status;

    @NotNull
    private List<Long> menuItemIds;

    @NotNull
    private Long userId;

    private Boolean paid;

    public @NotNull Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(@NotNull Integer totalCost) {
        this.totalCost = totalCost;
    }

    public @NotNull String getStatus() {
        return status;
    }

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public @NotNull List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(@NotNull List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public @NotNull Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
