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
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/menu-items")
@Tag(name = "4. Menu Item Management", description = "Menu Item Management API")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        return menuItemService.createMenuItem(menuItemDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return menuItemService.getMenuItemById(id);
    }

    @GetMapping
    public List<MenuItemDTO> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "salesCount") String sortBy,
            @RequestParam(defaultValue = "true") boolean desc) {
        return menuItemService.getAllMenuItems(page, limit, sortBy, desc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemDTO menuItemDTO) {
        return menuItemService.updateMenuItem(id, menuItemDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMenuItem(@PathVariable Long id) {
        return menuItemService.deleteMenuItem(id);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByIds(@RequestParam List<Long> ids) {
        return menuItemService.getMenuItemsByIds(ids);
    }
}
