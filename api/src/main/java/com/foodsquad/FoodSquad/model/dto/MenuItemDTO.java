package com.foodsquad.FoodSquad.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

public class MenuItemDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @URL
    @NotBlank(message = "ImageUrl cannot be blank")
    @Schema(defaultValue = "https://www.tastingtable.com/img/gallery/what-makes-restaurant-burgers-taste-different-from-homemade-burgers-upgrade/l-intro-1662064407.jpg")
    private String imageUrl;

    private Boolean defaultItem;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Total cost must be positive")
    private Double price;

    @NotNull(message = "Category cannot be null")
    @Pattern(regexp = "BURGER|PIZZA|SUSHI|PASTA|SALAD|TACOS|DESSERT|OTHER", message = "Category must be one of the following: BURGER, PIZZA, SUSHI,PASTA,SALAD,TACOS,DESSERT,OTHER")
    @Schema(defaultValue = "BURGER")
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Boolean defaultItem) {
        this.defaultItem = defaultItem;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
