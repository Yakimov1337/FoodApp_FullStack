package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.entity.MenuItem;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.OrderStatus;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.repository.MenuItemRepository;
import com.foodsquad.FoodSquad.repository.OrderRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<String> createOrder(OrderDTO orderDTO) {
        Order order = new Order();

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(orderDTO.getUserEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            order.setUser(user);
        } else {
            throw new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail());
        }

        // Set menu items
        List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
        if (menuItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one MenuItem");
        }
        order.setMenuItems(menuItems);

        // Set other fields
        order.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        order.setTotalCost(orderDTO.getTotalCost());
        order.setCreatedOn(orderDTO.getCreatedOn());
        order.setPaid(orderDTO.getPaid());

        orderRepository.save(order);
        return new ResponseEntity<>("Order successfully created.", HttpStatus.CREATED);
    }

    public List<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAllOrdersWithUsers(pageable);
        return orderPage.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findOrdersByUserId(userId, pageable);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findOrderWithUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return modelMapper.map(order, OrderDTO.class);
    }

    public ResponseEntity<String> updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> existingOrderOpt = orderRepository.findById(id);
        if (existingOrderOpt.isPresent()) {
            Order order = existingOrderOpt.get();

            // Update user
            Optional<User> userOpt = userRepository.findByEmail(orderDTO.getUserEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                order.setUser(user);
            } else {
                throw new IllegalArgumentException("User not found with email: " + orderDTO.getUserEmail());
            }

            // Update menu items
            List<MenuItem> menuItems = menuItemRepository.findAllById(orderDTO.getMenuItemIds());
            if (menuItems.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one MenuItem");
            }
            order.setMenuItems(menuItems);

            // Update other fields
            order.setStatus(OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
            order.setTotalCost(orderDTO.getTotalCost());
            order.setPaid(orderDTO.getPaid());

            orderRepository.save(order);
            return new ResponseEntity<>("Order successfully updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> deleteOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            orderRepository.delete(orderOpt.get());
            return new ResponseEntity<>("Order successfully deleted.", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }



    private OrderDTO convertToDTO(Order order) {
        List<Long> menuItemIds = order.getMenuItems().stream()
                .map(MenuItem::getId)
                .collect(Collectors.toList());
        return new OrderDTO(order.getId(), order.getUser().getEmail(), menuItemIds, order.getStatus(),
                order.getTotalCost(), order.getCreatedOn(), order.getPaid());
    }
}
