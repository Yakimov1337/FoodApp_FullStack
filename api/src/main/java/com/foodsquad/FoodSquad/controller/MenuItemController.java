package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.service.MenuItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/menu-items")
@Tag(name = "4. Menu Item Management", description = "Menu Item Management API")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<String> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        return menuItemService.createMenuItem(menuItemDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return menuItemService.getMenuItemById(id);
    }

    @GetMapping
    public List<MenuItemDTO> getAllMenuItems(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "20") int limit) {
        return menuItemService.getAllMenuItems(page, limit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemDTO menuItemDTO) {
        return menuItemService.updateMenuItem(id, menuItemDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id) {
        return menuItemService.deleteMenuItem(id);
    }
}
