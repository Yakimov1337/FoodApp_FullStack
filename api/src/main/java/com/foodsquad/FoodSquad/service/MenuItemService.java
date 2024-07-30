package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.*;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    public ResponseEntity<MenuItemDTO> createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = modelMapper.map(menuItemDTO, MenuItem.class);
        User currentUser = getCurrentUser();
        menuItem.setUser(currentUser); // owner
        if (menuItem.getDefaultItem() == null) {
            menuItem.setDefaultItem(false); // Ensure default is set to false (modelMapper causing mismatch)
        }
        menuItemRepository.save(menuItem);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        MenuItemDTO responseDTO = modelMapper.map(savedMenuItem, MenuItemDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    public ResponseEntity<MenuItemDTO> getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found for ID: " + id));
        int salesCount = orderRepository.countByMenuItemId(menuItem.getId());
        MenuItemDTO menuItemDTO = new MenuItemDTO(menuItem, salesCount);
        return ResponseEntity.ok(menuItemDTO);
    }

    public List<MenuItemDTO> getAllMenuItems(int page, int limit, String sortBy, boolean desc) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<MenuItem> menuItemPage = menuItemRepository.findAll(pageable);
        List<MenuItemDTO> menuItems = menuItemPage.stream()
                .map(menuItem -> {
                    int salesCount = orderRepository.countByMenuItemId(menuItem.getId());
                    return new MenuItemDTO(menuItem, salesCount);
                })
                .collect(Collectors.toList());

        // Sort by salesCount if specified (workaround for now)
        if ("salesCount".equals(sortBy)) {
            menuItems.sort((item1, item2) -> desc
                    ? item2.getSalesCount().compareTo(item1.getSalesCount())
                    : item1.getSalesCount().compareTo(item2.getSalesCount()));
        }

        return menuItems;
    }


    public ResponseEntity<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found for ID: " + id));

        checkOwnership(existingMenuItem);

        // Map the DTO to the existing entity, ensuring id field is skipped
        modelMapper.typeMap(MenuItemDTO.class, MenuItem.class)
                .addMappings(mapper -> mapper.skip(MenuItem::setId));
        modelMapper.map(menuItemDTO, existingMenuItem);

        // defaultItem field SHOULD NOT be null
        if (existingMenuItem.getDefaultItem() == null) {
            existingMenuItem.setDefaultItem(false);
        }

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        MenuItemDTO updatedMenuItemDTO = modelMapper.map(updatedMenuItem, MenuItemDTO.class);

        return ResponseEntity.ok(updatedMenuItemDTO);
    }

    public ResponseEntity<Map<String, String>> deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found for ID: " + id));
        checkOwnership(menuItem);
        menuItemRepository.delete(menuItem);
        return ResponseEntity.ok(Map.of("message", "Menu Item successfully deleted"));
    }

    //  method to fetch multiple menu items by their IDs
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByIds(List<Long> ids) {
        List<MenuItem> menuItems = menuItemRepository.findAllById(ids);
        if (menuItems.isEmpty()) {
            throw new EntityNotFoundException("No MenuItems found for the given IDs");
        }
        List<MenuItemDTO> menuItemDTOs = menuItems.stream()
                .map(menuItem -> {
                    int salesCount = orderRepository.countByMenuItemId(menuItem.getId());
                    return new MenuItemDTO(menuItem, salesCount);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuItemDTOs);
    }
}
