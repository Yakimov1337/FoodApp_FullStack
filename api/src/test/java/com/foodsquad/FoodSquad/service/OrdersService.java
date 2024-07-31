package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import com.foodsquad.FoodSquad.model.entity.User;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserEmail("user@example.com");
        orderDTO.setMenuItemIds(Arrays.asList(1L, 2L));
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setTotalCost(5000);
        orderDTO.setCreatedOn(LocalDateTime.now());

        User user = new User();
        user.setEmail("user@example.com");

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        List<MenuItem> menuItems = Arrays.asList(menuItem1, menuItem2);

        Order order = new Order();
        order.setUser(user);
        order.setMenuItems(menuItems);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalCost(5000);
        order.setCreatedOn(orderDTO.getCreatedOn());

        when(userRepository.findByEmail(orderDTO.getUserEmail())).thenReturn(Optional.of(user));
        when(menuItemRepository.findAllById(orderDTO.getMenuItemIds())).thenReturn(menuItems);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderService.createOrder(orderDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetAllOrders() {
        // Arrange
        int page = 0;
        int size = 10;
        Order order = new Order();
        order.setId("order_id");
        order.setTotalCost(5000);
        order.setCreatedOn(LocalDateTime.now());

        Page<Order> orderPage = mock(Page.class);
        when(orderPage.getContent()).thenReturn(Arrays.asList(order));
        when(orderRepository.findAllOrdersWithUsers(any(Pageable.class))).thenReturn(orderPage);
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId("order_id");
        orderDTO.setTotalCost(5000);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        // Act
        List<OrderDTO> orders = orderService.getAllOrders(page, size);

        // Assert
        assertEquals(1, orders.size());
        assertEquals("order_id", orders.get(0).getId());
    }

    @Test
    void testGetOrderById() {
        // Arrange
        String orderId = "order_id";
        Order order = new Order();
        order.setId(orderId);
        order.setTotalCost(5000);
        order.setCreatedOn(LocalDateTime.now());

        when(orderRepository.findOrderWithUserById(orderId)).thenReturn(Optional.of(order));
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);
        orderDTO.setTotalCost(5000);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderService.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void testUpdateOrder() {
        // Arrange
        String orderId = "order_id";
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserEmail("user@example.com");
        orderDTO.setMenuItemIds(Arrays.asList(1L, 2L));
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setTotalCost(5000);
        orderDTO.setPaid(false);

        Order order = new Order();
        order.setId(orderId);
        order.setTotalCost(5000);
        order.setCreatedOn(LocalDateTime.now());

        User user = new User();
        user.setEmail("user@example.com");

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        List<MenuItem> menuItems = Arrays.asList(menuItem1, menuItem2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail(orderDTO.getUserEmail())).thenReturn(Optional.of(user));
        when(menuItemRepository.findAllById(orderDTO.getMenuItemIds())).thenReturn(menuItems);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderService.updateOrder(orderId, orderDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        String orderId = "order_id";
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        ResponseEntity<Map<String, String>> response = orderService.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order successfully deleted", response.getBody().get("message"));
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        String orderId = "invalid_order_id";
        when(orderRepository.findOrderWithUserById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(orderId));
    }
}
