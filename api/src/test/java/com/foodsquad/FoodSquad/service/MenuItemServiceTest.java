package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.MenuItemCategory;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.ReviewRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext("test@example.com");
    }

    @Test
    void testCreateMenuItem() {
        // Arrange
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setTitle("Burger");
        menuItemDTO.setDescription("Delicious burger");
        menuItemDTO.setPrice(10.0);
        menuItemDTO.setCategory("BURGER");
        menuItemDTO.setImageUrl("http://example.com/burger.jpg");

        User user = new User();
        user.setEmail("test@example.com");

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setUser(user);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);
        when(modelMapper.map(any(MenuItemDTO.class), eq(MenuItem.class))).thenReturn(menuItem);
        when(modelMapper.map(any(MenuItem.class), eq(MenuItemDTO.class))).thenReturn(menuItemDTO);

        // Act
        ResponseEntity<MenuItemDTO> response = menuItemService.createMenuItem(menuItemDTO);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Burger", response.getBody().getTitle());
    }

    @Test
    void testGetMenuItemById() {
        // Arrange
        Long menuItemId = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setTitle("Burger");

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(orderRepository.sumQuantityByMenuItemId(menuItemId)).thenReturn(10);
        when(reviewRepository.countByMenuItemId(menuItemId)).thenReturn(5L);
        when(reviewRepository.findAverageRatingByMenuItemId(menuItemId)).thenReturn(4.5);

        MenuItemDTO menuItemDTO = new MenuItemDTO(menuItem, 10, 5, 4.5);
        when(modelMapper.map(any(MenuItem.class), eq(MenuItemDTO.class))).thenReturn(menuItemDTO);

        // Act
        ResponseEntity<MenuItemDTO> response = menuItemService.getMenuItemById(menuItemId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Burger", response.getBody().getTitle());
        assertEquals(10, response.getBody().getSalesCount());
        assertEquals(5, response.getBody().getReviewCount());
        assertEquals(4.5, response.getBody().getAverageRating());
    }

    @Test
    void testUpdateMenuItem() {
        // Arrange
        Long menuItemId = 1L;
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setTitle("Updated Burger");
        menuItemDTO.setDescription("Updated description");
        menuItemDTO.setPrice(12.0);
        menuItemDTO.setCategory("BURGER");
        menuItemDTO.setImageUrl("http://example.com/updated_burger.jpg");

        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);

        MenuItem existingMenuItem = new MenuItem();
        existingMenuItem.setId(menuItemId);
        existingMenuItem.setUser(user);

        TypeMap<MenuItemDTO, MenuItem> typeMap = mock(TypeMap.class);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(existingMenuItem));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(existingMenuItem);
        when(modelMapper.typeMap(MenuItemDTO.class, MenuItem.class)).thenReturn(typeMap);
        when(modelMapper.map(any(MenuItem.class), eq(MenuItemDTO.class))).thenReturn(menuItemDTO);

        // Act
        ResponseEntity<MenuItemDTO> response = menuItemService.updateMenuItem(menuItemId, menuItemDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Updated Burger", response.getBody().getTitle());
        assertEquals("Updated description", response.getBody().getDescription());
        assertEquals(12.0, response.getBody().getPrice());
        assertEquals("http://example.com/updated_burger.jpg", response.getBody().getImageUrl());
    }

    @Test
    void testDeleteMenuItem() {
        // Arrange
        Long menuItemId = 1L;
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.NORMAL);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setUser(user);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, String>> response = menuItemService.deleteMenuItem(menuItemId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Menu Item successfully deleted", response.getBody().get("message"));
        verify(menuItemRepository, times(1)).delete(menuItem);
    }

    private void mockSecurityContext(String email) {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", Collections.emptyList()));
    }
}
