package com.foodsquad.FoodSquad.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public class MenuItemDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @URL
    private String imageUrl;

    private Boolean defaultItem;

    @NotNull
    private Double price;

    private String category;

    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }

    public @URL String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@URL String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Boolean defaultItem) {
        this.defaultItem = defaultItem;
    }

    public @NotNull Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
