package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.OrderRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderRepository orderRepository;

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


    public List<UserResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.stream()
                .map(user -> {
                    long ordersCount = orderRepository.countByUserId(user.getId());
                    UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
                    userResponseDTO.setOrdersCount(ordersCount);
                    return userResponseDTO;
                })
                .collect(Collectors.toList());
    }

    public ResponseEntity<UserResponseDTO> getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));
        long ordersCount = orderRepository.countByUserId(id);
        UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
        userDTO.setOrdersCount(ordersCount);
        return ResponseEntity.ok(userDTO);
    }

    @Transactional
    public ResponseEntity<UserResponseDTO> updateUser(String id, UserResponseDTO userResponseDTO) {
        checkOwnership(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));

        // Check if the user role is ADMIN and is being changed
        if (user.getRole() == UserRole.ADMIN && !userResponseDTO.getRole().equals(UserRole.ADMIN.name())) {
            throw new IllegalArgumentException("Admin user role cannot be changed");
        }

        user.setName(userResponseDTO.getName());
        user.setEmail(userResponseDTO.getEmail());
        user.setRole(UserRole.valueOf(userResponseDTO.getRole()));
        user.setImageUrl(userResponseDTO.getImageUrl());
        user.setPhoneNumber(userResponseDTO.getPhoneNumber());

        userRepository.save(user);
        long ordersCount = orderRepository.countByUserId(id);
        UserResponseDTO updatedUserDTO = modelMapper.map(user, UserResponseDTO.class);
        updatedUserDTO.setOrdersCount(ordersCount);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));
        if (user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("Admin users cannot be deleted.");
        }

        // Delete menu item references in order_menu_item (w/o this foreign key table error appears)
        user.getMenuItems().forEach(menuItem -> {
            orderRepository.removeMenuItemReferences(menuItem.getId());
        });

        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message","User successfully deleted."));
    }
}
