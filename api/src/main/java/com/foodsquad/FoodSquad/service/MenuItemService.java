package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<String> createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = modelMapper.map(menuItemDTO, MenuItem.class);
        menuItemRepository.save(menuItem);
        return new ResponseEntity<>("Menu Item successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<MenuItemDTO> getMenuItemById(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
            MenuItemDTO menuItemDTO = modelMapper.map(menuItem.get(), MenuItemDTO.class);
            return ResponseEntity.ok(menuItemDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        List<MenuItemDTO> menuItemDTOs = menuItems.stream()
                .map(menuItem -> modelMapper.map(menuItem, MenuItemDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuItemDTOs);
    }

    public ResponseEntity<String> updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        Optional<MenuItem> existingMenuItem = menuItemRepository.findById(id);
        if (existingMenuItem.isPresent()) {
            MenuItem menuItem = existingMenuItem.get();
            modelMapper.map(menuItemDTO, menuItem);
            menuItemRepository.save(menuItem);
            return ResponseEntity.ok("Menu Item successfully updated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found");
        }
    }

    public ResponseEntity<String> deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return ResponseEntity.ok("Menu Item successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found");
        }
    }
}
