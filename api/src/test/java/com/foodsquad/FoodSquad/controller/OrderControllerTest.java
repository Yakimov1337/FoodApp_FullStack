package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() {
        OrderDTO orderDTO = new OrderDTO();
        OrderDTO createdOrder = new OrderDTO();
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(new ResponseEntity<>(createdOrder, HttpStatus.CREATED));

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdOrder, response.getBody());
        verify(orderService, times(1)).createOrder(any(OrderDTO.class));
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        String orderId = "1";
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.getOrderById(orderId)).thenReturn(new ResponseEntity<>(orderDTO, HttpStatus.OK));

        ResponseEntity<OrderDTO> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void getOrdersByUserId_shouldReturnOrderList() {
        String userId = "1";
        int page = 0;
        int size = 10;
        List<OrderDTO> orders = List.of(new OrderDTO());
        when(orderService.getOrdersByUserId(userId, page, size)).thenReturn(orders);

        List<OrderDTO> response = orderController.getOrdersByUserId(userId, page, size);

        assertEquals(orders, response);
        verify(orderService, times(1)).getOrdersByUserId(userId, page, size);
    }

    @Test
    void getAllOrders_shouldReturnOrderList() {
        int page = 0;
        int size = 10;
        List<OrderDTO> orders = List.of(new OrderDTO());
        when(orderService.getAllOrders(page, size)).thenReturn(orders);

        List<OrderDTO> response = orderController.getAllOrders(page, size);

        assertEquals(orders, response);
        verify(orderService, times(1)).getAllOrders(page, size);
    }

    @Test
    void updateOrder_shouldReturnUpdatedOrder() {
        String orderId = "1";
        OrderDTO orderDTO = new OrderDTO();
        OrderDTO updatedOrder = new OrderDTO();
        when(orderService.updateOrder(any(String.class), any(OrderDTO.class))).thenReturn(new ResponseEntity<>(updatedOrder, HttpStatus.OK));

        ResponseEntity<OrderDTO> response = orderController.updateOrder(orderId, orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrder, response.getBody());
        verify(orderService, times(1)).updateOrder(any(String.class), any(OrderDTO.class));
    }

    @Test
    void deleteOrder_shouldReturnSuccessMessage() {
        String orderId = "1";
        Map<String, String> responseMap = Map.of("message", "Order successfully deleted");
        when(orderService.deleteOrder(orderId)).thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        ResponseEntity<Map<String, String>> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseMap, response.getBody());
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void deleteOrders_shouldReturnSuccessMessage() {
        List<String> orderIds = List.of("1", "2", "3");
        Map<String, String> responseMap = Map.of("message", "Orders successfully deleted");
        when(orderService.deleteOrders(orderIds)).thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        ResponseEntity<Map<String, String>> response = orderController.deleteMenuItemsByIds(orderIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseMap, response.getBody());
        verify(orderService, times(1)).deleteOrders(orderIds);
    }

}
