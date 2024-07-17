package com.foodsquad.FoodSquad.model.dto;

import java.time.LocalDateTime;
import java.util.List;
public class OrderResponseDTO {
    private Long id;
    private Integer totalCost;
    private String status;
    private LocalDateTime createdOn;
    private List<MenuItemResponseDTO> menuItems;
    private UserResponseDTO user;
    private Boolean paid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public List<MenuItemResponseDTO> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemResponseDTO> menuItems) {
        this.menuItems = menuItems;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
