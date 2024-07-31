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
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void checkOwnership(String userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getRole().equals(UserRole.MODERATOR)) {
            throw new IllegalArgumentException("Access denied");
        }
    }

    public ResponseEntity<OrderDTO> createOrder(OrderDTO orderDTO) {
        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail()));

        List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
        if (menuItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one MenuItem");
        }

        Order order = new Order();
        order.setUser(user);
        order.setMenuItems(menuItems);
        order.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        order.setTotalCost(orderDTO.getTotalCost());
        order.setCreatedOn(orderDTO.getCreatedOn());
        order.setPaid(orderDTO.getPaid());

        orderRepository.save(order);
        Order savedOrder = orderRepository.save(order);
        OrderDTO responseDTO = modelMapper.map(savedOrder, OrderDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    public List<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAllOrdersWithUsers(pageable);
        return orderPage.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUserId(String userId, int page, int size) {
        checkOwnership(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findOrdersByUserId(userId, pageable);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<OrderDTO> getOrderById(String id) {
        Order order = orderRepository.findOrderWithUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.ok(orderDTO);
    }

    public ResponseEntity<OrderDTO> updateOrder(String id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));

        // Update user
        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail()));
        existingOrder.setUser(user);

        // Update menu items
        List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
        if (menuItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one MenuItem");
        }
        existingOrder.setMenuItems(menuItems);

        // Update other fields
        existingOrder.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        existingOrder.setTotalCost(orderDTO.getTotalCost());
        existingOrder.setPaid(orderDTO.getPaid());

        orderRepository.save(existingOrder);
        OrderDTO updatedOrderDTO = modelMapper.map(existingOrder, OrderDTO.class);
        return ResponseEntity.ok(updatedOrderDTO);
    }


    public ResponseEntity<Map<String, String>> deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));
        orderRepository.delete(order);
        return ResponseEntity.ok(Map.of("message", "Order successfully deleted"));
    }


    private OrderDTO convertToDTO(Order order) {
        List<Long> menuItemIds = order.getMenuItems().stream()
                .map(MenuItem::getId)
                .collect(Collectors.toList());
        return new OrderDTO(order.getId(), order.getUser().getEmail(), menuItemIds, order.getStatus(),
                order.getTotalCost(), order.getCreatedOn(), order.getPaid());
    }
}
