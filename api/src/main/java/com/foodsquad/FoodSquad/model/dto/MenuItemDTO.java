package com.foodsquad.FoodSquad.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.MenuItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.stream.Collectors;

public class MenuItemDTO {
    //allows reading while returning the object only, TODO: create additional MenuItemCreateDTO
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

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
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Price cannot be null")
//    @Pattern(regexp = "BURGER|PIZZA|SUSHI|PASTA|SALAD|TACOS|DESSERT|OTHER", message = "Category must be one of the following: BURGER, PIZZA, SUSHI,PASTA,SALAD,TACOS,DESSERT,OTHER")
    @Schema(defaultValue = "BURGER")
    private MenuItemCategory category;

    //allows reading while returning the object only, TODO: create additional MenuItemCreateDTO
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer salesCount;

    // Default constructor
    public MenuItemDTO() {
    }

    // Constructor to create DTO from MenuItem entity
    public MenuItemDTO(MenuItem menuItem, int salesCount) {
        this.id = menuItem.getId();
        this.title = menuItem.getTitle();
        this.description = menuItem.getDescription();
        this.imageUrl = menuItem.getImageUrl();
        this.defaultItem = menuItem.getDefaultItem();
        this.price = menuItem.getPrice();
        this.category = menuItem.getCategory();
        this.salesCount = salesCount;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public @NotBlank(message = "Title cannot be blank") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title cannot be blank") String title) {
        this.title = title;
    }

    public @NotBlank(message = "Description cannot be blank") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Description cannot be blank") String description) {
        this.description = description;
    }

    public @URL @NotBlank(message = "ImageUrl cannot be blank") String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@URL @NotBlank(message = "ImageUrl cannot be blank") String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Boolean defaultItem) {
        this.defaultItem = defaultItem;
    }

    public @NotNull(message = "Price cannot be null") @Positive(message = "Price must be positive") Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price cannot be null") @Positive(message = "Price must be positive") Double price) {
        this.price = price;
    }

    public  MenuItemCategory getCategory() {
        return category;
    }

    public void setCategory() {
        this.category = category;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }
}
