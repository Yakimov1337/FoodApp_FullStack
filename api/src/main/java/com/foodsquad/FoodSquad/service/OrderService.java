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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
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

    private void checkOwnership(User owner) {
        User currentUser = getCurrentUser();
        if (!currentUser.equals(owner) && !currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getRole().equals(UserRole.MODERATOR)) {
            throw new IllegalArgumentException("Access denied");
        }
    }

    public ResponseEntity<OrderDTO> createOrder(OrderDTO orderDTO) {
        if (orderDTO.getMenuItemQuantities() == null || orderDTO.getMenuItemQuantities().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one menu item");
        }
        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail()));

        Map<Long, Integer> menuItemQuantities = orderDTO.getMenuItemQuantities();
        Map<MenuItem, Integer> menuItemsWithQuantity = new HashMap<>();
        Double totalCost = 0.0;

        for (Map.Entry<Long, Integer> entry : menuItemQuantities.entrySet()) {
            MenuItem menuItem = menuItemRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + entry.getKey()));
            int quantity = entry.getValue() != null ? entry.getValue() : 1; // Default to 1 if quantity is null
            menuItemsWithQuantity.put(menuItem, quantity);
            totalCost += menuItem.getPrice() * quantity;
        }
        // Round the totalCost to 2 decimal places
        BigDecimal roundedTotalCost = BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setUser(user);
        order.setMenuItemsWithQuantity(menuItemsWithQuantity);
        order.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        order.setTotalCost(roundedTotalCost.doubleValue());
        order.setCreatedOn(orderDTO.getCreatedOn());
        order.setPaid(orderDTO.getPaid());

        orderRepository.save(order);
        OrderDTO responseDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    public List<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<Order> orderPage = orderRepository.findAllOrdersWithUsers(pageable);
        return orderPage.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUserId(String userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        checkOwnership(user);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findOrdersByUserId(userId, pageable);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<OrderDTO> getOrderById(String id) {
        Order order = orderRepository.findOrderWithUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));
        checkOwnership(order.getUser());
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.ok(orderDTO);
    }

    public ResponseEntity<OrderDTO> updateOrder(String id, OrderDTO orderDTO) {
        if (orderDTO.getMenuItemQuantities() == null || orderDTO.getMenuItemQuantities().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one menu item");
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));
        checkOwnership(existingOrder.getUser());

        // Update user
        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail()));
        existingOrder.setUser(user);

        // Update menu items with quantities
        Map<Long, Integer> menuItemQuantities = orderDTO.getMenuItemQuantities();
        Map<MenuItem, Integer> menuItemsWithQuantity = new HashMap<>();
        Double totalCost = 0.0;

        for (Map.Entry<Long, Integer> entry : menuItemQuantities.entrySet()) {
            MenuItem menuItem = menuItemRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + entry.getKey()));
            int quantity = entry.getValue() != null ? entry.getValue() : 1; // Default to 1 if quantity is null
            menuItemsWithQuantity.put(menuItem, quantity);
            totalCost += menuItem.getPrice() * quantity;
        }
        // Round the totalCost to 2 decimal places
        BigDecimal roundedTotalCost = BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP);

        existingOrder.setMenuItemsWithQuantity(menuItemsWithQuantity);

        // Update other fields
        existingOrder.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        existingOrder.setTotalCost(roundedTotalCost.doubleValue());
        existingOrder.setCreatedOn(orderDTO.getCreatedOn());
        existingOrder.setPaid(orderDTO.getPaid());

        orderRepository.save(existingOrder);
        OrderDTO updatedOrderDTO = modelMapper.map(existingOrder, OrderDTO.class);
        return ResponseEntity.ok(updatedOrderDTO);
    }


    public ResponseEntity<Map<String, String>> deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + id));
        checkOwnership(order.getUser());
        orderRepository.delete(order);
        return ResponseEntity.ok(Map.of("message", "Order successfully deleted"));
    }

    public ResponseEntity<Map<String, String>> deleteOrders(List<String> ids) {
        List<Order> orders = orderRepository.findAllById(ids);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No Orders found for the given IDs");
        }
        for (Order order : orders) {
            checkOwnership(order.getUser());
        }
        orderRepository.deleteAll(orders);
        return ResponseEntity.ok(Map.of("message", "Orders successfully deleted"));
    }

}
