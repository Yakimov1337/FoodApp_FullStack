package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.*;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext("test@example.com");
    }

    @Test
    void testCreateOrder() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserEmail("test@example.com");
        orderDTO.setMenuItemQuantities(Map.of(1L, 2));
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setCreatedOn(LocalDateTime.now());
        orderDTO.setPaid(false);

        User user = new User();
        user.setEmail("test@example.com");

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setPrice(10.0);

        Order order = new Order();
        order.setUser(user);
        order.setMenuItemsWithQuantity(Map.of(menuItem, 2));
        order.setTotalCost(20.0);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenAnswer(invocation -> {
            Order source = invocation.getArgument(0);
            return new OrderDTO(source.getId(), source.getUser().getEmail(), Map.of(1L, 2), source.getStatus(), source.getTotalCost(), source.getCreatedOn(), source.getPaid());
        });

        // Act
        ResponseEntity<OrderDTO> response = orderService.createOrder(orderDTO);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(20.0, response.getBody().getTotalCost());
    }

    @Test
    void testGetAllOrders() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        orders.add(order);
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

        when(orderRepository.findAllOrdersWithUsers(any(PageRequest.class))).thenReturn(orderPage);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        // Act
        List<OrderDTO> response = orderService.getAllOrders(0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }
    @Test
    void testGetOrderById() {
        // Arrange
        String orderId = "order123";
        User user = new User();
        user.setEmail("test@example.com");
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);

        when(orderRepository.findOrderWithUserById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderService.getOrderById(orderId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().getId());
    }

    @Test
    void testGetOrdersByUserId() {
        // Arrange
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        orders.add(order);
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findOrdersByUserId(eq(userId), any(PageRequest.class))).thenReturn(orderPage);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        // Act
        List<OrderDTO> response = orderService.getOrdersByUserId(userId, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }


    @Test
    void testUpdateOrder() {
        // Arrange
        String orderId = "order123";
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserEmail("test@example.com");
        orderDTO.setMenuItemQuantities(Map.of(1L, 2));
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setCreatedOn(LocalDateTime.now());
        orderDTO.setPaid(false);

        User user = new User();
        user.setEmail("test@example.com");

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setPrice(10.0);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setUser(user);

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setUser(user);
        updatedOrder.setMenuItemsWithQuantity(Map.of(menuItem, 2));
        updatedOrder.setTotalCost(20.0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenAnswer(invocation -> {
            Order source = invocation.getArgument(0);
            return new OrderDTO(source.getId(), source.getUser().getEmail(), Map.of(1L, 2), source.getStatus(), source.getTotalCost(), source.getCreatedOn(), source.getPaid());
        });

        // Act
        ResponseEntity<OrderDTO> response = orderService.updateOrder(orderId, orderDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(20.0, response.getBody().getTotalCost());
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        String orderId = "order123";
        User user = new User();
        user.setEmail("test@example.com");
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, String>> response = orderService.deleteOrder(orderId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Order successfully deleted", response.getBody().get("message"));
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrders() {
        // Arrange
        List<String> orderIds = Arrays.asList("order123", "order124");
        User user = new User();
        user.setEmail("test@example.com");

        Order order1 = new Order();
        order1.setId("order123");
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId("order124");
        order2.setUser(user);

        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findAllById(orderIds)).thenReturn(orders);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, String>> response = orderService.deleteOrders(orderIds);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Orders successfully deleted", response.getBody().get("message"));
        verify(orderRepository, times(1)).deleteAll(orders);
    }

    private void mockSecurityContext(String email) {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", Collections.emptyList()));
    }
}

