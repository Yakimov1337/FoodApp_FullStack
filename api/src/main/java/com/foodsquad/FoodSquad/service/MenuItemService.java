package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.dto.PaginatedResponseDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.MenuItemCategory;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.ReviewRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        Integer salesCount = orderRepository.sumQuantityByMenuItemId(menuItem.getId());
        if (salesCount == null) {
            salesCount = 0;
        }
        long reviewCount = reviewRepository.countByMenuItemId(menuItem.getId());
        Double averageRating = reviewRepository.findAverageRatingByMenuItemId(menuItem.getId());
        if (averageRating == null) {
            averageRating = 0.0; // Default to 0.0 if there are no reviews
        }
        averageRating = Math.round(averageRating * 10.0) / 10.0; // Format to 1 decimal place
        MenuItemDTO menuItemDTO = new MenuItemDTO(menuItem, salesCount, reviewCount, averageRating);
        return ResponseEntity.ok(menuItemDTO);
    }

    public PaginatedResponseDTO<MenuItemDTO> getAllMenuItems(int page, int limit, String sortBy, boolean desc, String categoryFilter, String isDefault, String priceSortDirection) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<MenuItem> menuItemPage;

        // Filter by item category
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            MenuItemCategory category = MenuItemCategory.valueOf(categoryFilter.toUpperCase());
            menuItemPage = menuItemRepository.findByCategory(category, pageable);
        } else {
            menuItemPage = menuItemRepository.findAll(pageable);
        }

        List<MenuItemDTO> menuItems = menuItemPage.stream()
                .map(menuItem -> {
                    Integer salesCount = orderRepository.sumQuantityByMenuItemId(menuItem.getId());
                    if (salesCount == null) {
                        salesCount = 0;
                    }
                    long reviewCount = reviewRepository.countByMenuItemId(menuItem.getId());
                    Double averageRating = reviewRepository.findAverageRatingByMenuItemId(menuItem.getId());
                    if (averageRating == null) {
                        averageRating = 0.0; // Default to 0.0 if there are no reviews
                    }
                    averageRating = Math.round(averageRating * 10.0) / 10.0; // Format to 1 decimal place
                    return new MenuItemDTO(menuItem, salesCount, reviewCount, averageRating);
                })
                .collect(Collectors.toList());

        // Filter by default item
        if (isDefault != null && !isDefault.isEmpty()) {
            boolean defaultFlag = isDefault.equalsIgnoreCase("true");
            menuItems = menuItems.stream().filter(item -> item.getDefaultItem().equals(defaultFlag)).collect(Collectors.toList());
        }

        // Sort by price
        if (priceSortDirection != null && !priceSortDirection.isEmpty()) {
            menuItems.sort((a, b) -> {
                if (priceSortDirection.equals("asc")) {
                    return Double.compare(a.getPrice(), b.getPrice());
                } else {
                    return Double.compare(b.getPrice(), a.getPrice());
                }
            });
        }

        // Sort by salesCount if specified (workaround for now)
        if ("salesCount".equals(sortBy)) {
            menuItems.sort((item1, item2) -> desc
                    ? item2.getSalesCount().compareTo(item1.getSalesCount())
                    : item1.getSalesCount().compareTo(item2.getSalesCount()));
        }

        // PaginatedResponseDTO with the list of items and the total count
        return new PaginatedResponseDTO<>(menuItems, menuItemPage.getTotalElements());
    }

    @Transactional
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

    @Transactional
    public ResponseEntity<Map<String, String>> deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found for ID: " + id));
        checkOwnership(menuItem);

        // Remove references in order_menu_item table (w/o this foreign key error appears)
        orderRepository.removeMenuItemReferences(menuItem.getId());

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
                    Integer salesCount = orderRepository.sumQuantityByMenuItemId(menuItem.getId());
                    if (salesCount == null) {
                        salesCount = 0;
                    }
                    long reviewCount = reviewRepository.countByMenuItemId(menuItem.getId());
                    Double averageRating = reviewRepository.findAverageRatingByMenuItemId(menuItem.getId());
                    if (averageRating == null) {
                        averageRating = 0.0; // Default to 0.0 if there are no reviews
                    }
                    averageRating = Math.round(averageRating * 10.0) / 10.0; // Format to 1 decimal place
                    return new MenuItemDTO(menuItem, salesCount, reviewCount, averageRating);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuItemDTOs);
    }


    // Method to delete multiple menu items by their IDs
    @Transactional
    public ResponseEntity<Map<String, String>> deleteMenuItemsByIds(List<Long> ids) {
        List<MenuItem> menuItems = menuItemRepository.findAllById(ids);
        if (menuItems.isEmpty()) {
            throw new EntityNotFoundException("No MenuItems found for the given IDs");
        }
        menuItems.forEach(menuItem -> {
            checkOwnership(menuItem);
            orderRepository.removeMenuItemReferences(menuItem.getId()); // Remove references in order_menu_item table
            menuItemRepository.delete(menuItem);
        });
        return ResponseEntity.ok(Map.of("message", "Menu Items successfully deleted"));
    }
}
