package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.MenuItemDTO;
import com.foodsquad.FoodSquad.model.dto.PaginatedResponseDTO;
import com.foodsquad.FoodSquad.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuItemControllerTest {

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMenuItems_shouldReturnPaginatedResponse() {
        PaginatedResponseDTO<MenuItemDTO> paginatedResponse = new PaginatedResponseDTO<>(Collections.singletonList(new MenuItemDTO()), 1L);
        when(menuItemService.getAllMenuItems(anyInt(), anyInt(), anyString(), anyBoolean(), anyString(), anyString(), anyString())).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponseDTO<MenuItemDTO>> response = menuItemController.getAllMenuItems(0, 10, "salesCount", true, "BURGER", "true", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paginatedResponse, response.getBody());
        verify(menuItemService, times(1)).getAllMenuItems(0, 10, "salesCount", true, "BURGER", "true", "asc");
    }

    @Test
    void testCreateMenuItem() {
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setTitle("Burger");
        when(menuItemService.createMenuItem(any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(menuItemDTO));

        ResponseEntity<MenuItemDTO> response = menuItemController.createMenuItem(menuItemDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItemDTO, response.getBody());
        verify(menuItemService, times(1)).createMenuItem(any(MenuItemDTO.class));
    }

    @Test
    void testGetMenuItemById() {
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        when(menuItemService.getMenuItemById(anyLong())).thenReturn(ResponseEntity.ok(menuItemDTO));

        ResponseEntity<MenuItemDTO> response = menuItemController.getMenuItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItemDTO, response.getBody());
        verify(menuItemService, times(1)).getMenuItemById(1L);
    }

    @Test
    void testUpdateMenuItem() {
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        when(menuItemService.updateMenuItem(anyLong(), any(MenuItemDTO.class))).thenReturn(ResponseEntity.ok(menuItemDTO));

        ResponseEntity<MenuItemDTO> response = menuItemController.updateMenuItem(1L, menuItemDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItemDTO, response.getBody());
        verify(menuItemService, times(1)).updateMenuItem(anyLong(), any(MenuItemDTO.class));
    }

    @Test
    void testDeleteMenuItem() {
        when(menuItemService.deleteMenuItem(anyLong())).thenReturn(ResponseEntity.ok(Collections.singletonMap("message", "Menu Item successfully deleted")));

        ResponseEntity<Map<String, String>> response = menuItemController.deleteMenuItem(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Menu Item successfully deleted", response.getBody().get("message"));
        verify(menuItemService, times(1)).deleteMenuItem(1L);
    }

    @Test
    void testGetMenuItemsByIds() {
        List<MenuItemDTO> menuItems = Collections.singletonList(new MenuItemDTO());
        when(menuItemService.getMenuItemsByIds(anyList())).thenReturn(ResponseEntity.ok(menuItems));

        List<Long> menuItemIds = List.of(1L, 2L, 3L);
        ResponseEntity<List<MenuItemDTO>> response = menuItemController.getMenuItemsByIds(menuItemIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItems, response.getBody());
        verify(menuItemService, times(1)).getMenuItemsByIds(anyList());
    }


    @Test
    void testDeleteMenuItemsByIds() {
        when(menuItemService.deleteMenuItemsByIds(anyList())).thenReturn(ResponseEntity.ok(Collections.singletonMap("message", "Menu Items successfully deleted")));

        List<Long> menuItemIds = List.of(1L, 2L, 3L);
        ResponseEntity<Map<String, String>> response = menuItemController.deleteMenuItemsByIds(menuItemIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Menu Items successfully deleted", response.getBody().get("message"));
        verify(menuItemService, times(1)).deleteMenuItemsByIds(anyList());
    }

}
