package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ResponseEntity<String> createOrder(OrderDTO orderDTO) {
        Order order = new Order();

        // Set user
        Optional<User> user = userRepository.findById(orderDTO.getUserId());
        if (user.isPresent()) {
            order.setUser(user.get());
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }

        // Set menu items
        List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
        order.setMenuItems(menuItems);

        // Set other fields
        order.setStatus(orderDTO.getStatus());
        order.setTotalCost(orderDTO.getTotalCost());
        order.setCreatedOn(orderDTO.getCreatedOn());
        order.setPaid(orderDTO.getPaid());

        orderRepository.save(order);
        return new ResponseEntity<>("Order successfully created.", HttpStatus.CREATED);
    }

    public OrderDTO getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return new OrderDTO(order.get());
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();

            // Update fields
            List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
            order.setMenuItems(menuItems);
            order.setStatus(orderDTO.getStatus());
            order.setTotalCost(orderDTO.getTotalCost());
            order.setPaid(orderDTO.getPaid());

            orderRepository.save(order);
            return new ResponseEntity<>("Order successfully updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return new ResponseEntity<>("Order successfully deleted.", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }
}
