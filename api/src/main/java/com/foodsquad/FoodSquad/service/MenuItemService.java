package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void checkOwnership(MenuItem menuItem) {
        User currentUser = getCurrentUser();
        if (!menuItem.getUser().equals(currentUser) && !currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getRole().equals(UserRole.MODERATOR)) {
            throw new IllegalArgumentException("Access denied");
        }
    }

    public ResponseEntity<String> createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = modelMapper.map(menuItemDTO, MenuItem.class);
        User currentUser = getCurrentUser();
        menuItem.setUser(currentUser); // owner
        menuItemRepository.save(menuItem);
        return new ResponseEntity<>("Menu Item successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<MenuItemDTO> getMenuItemById(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
//            checkOwnership(menuItem.get());
            MenuItemDTO menuItemDTO = modelMapper.map(menuItem.get(), MenuItemDTO.class);
            return ResponseEntity.ok(menuItemDTO);
        }else {
            throw new IllegalArgumentException("Menu Item not found");
        }
    }

    public List<MenuItemDTO> getAllMenuItems(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<MenuItem> menuItemPage = menuItemRepository.findAll(pageable);
        return menuItemPage.stream()
                .map(MenuItemDTO::new)
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        Optional<MenuItem> existingMenuItem = menuItemRepository.findById(id);
        if (existingMenuItem.isPresent()) {
            MenuItem menuItem = existingMenuItem.get();
            checkOwnership(menuItem);
            modelMapper.map(menuItemDTO, menuItem);
            menuItemRepository.save(menuItem);
            return ResponseEntity.ok("Menu Item successfully updated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found");
        }
    }

    public ResponseEntity<String> deleteMenuItem(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
            checkOwnership(menuItem.get());
            menuItemRepository.delete(menuItem.get());
            return ResponseEntity.ok("Menu Item successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found");
        }
    }
}
