package com.foodsquad.FoodSquad.model.dto;

import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.MenuItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.stream.Collectors;

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
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Category cannot be null")
    @Pattern(regexp = "BURGER|PIZZA|SUSHI|PASTA|SALAD|TACOS|DESSERT|OTHER", message = "Category must be one of the following: BURGER, PIZZA, SUSHI,PASTA,SALAD,TACOS,DESSERT,OTHER")
    @Schema(defaultValue = "BURGER")
    private MenuItemCategory category;


    private List<OrderDTO> orders;

    // Default constructor
    public MenuItemDTO() {}

    // Constructor to create DTO from MenuItem entity
    public MenuItemDTO(MenuItem menuItem) {
        this.title = menuItem.getTitle();
        this.description = menuItem.getDescription();
        this.imageUrl = menuItem.getImageUrl();
        this.defaultItem = menuItem.getDefaultItem();
        this.price = menuItem.getPrice();
        this.category = menuItem.getCategory();
        this.orders = menuItem.getOrders().stream().map(OrderDTO::new).collect(Collectors.toList());
    }

    // getters and setters

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

    public @NotNull(message = "Category cannot be null") @Pattern(regexp = "BURGER|PIZZA|SUSHI|PASTA|SALAD|TACOS|DESSERT|OTHER", message = "Category must be one of the following: BURGER, PIZZA, SUSHI,PASTA,SALAD,TACOS,DESSERT,OTHER") MenuItemCategory getCategory() {
        return category;
    }

    public void setCategory(@NotNull(message = "Category cannot be null") @Pattern(regexp = "BURGER|PIZZA|SUSHI|PASTA|SALAD|TACOS|DESSERT|OTHER", message = "Category must be one of the following: BURGER, PIZZA, SUSHI,PASTA,SALAD,TACOS,DESSERT,OTHER") MenuItemCategory category) {
        this.category = category;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
}
