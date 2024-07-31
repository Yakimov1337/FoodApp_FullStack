package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.OrderDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.Order;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
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
public class UserService {
    @Autowired
    private UserRepository userRepository;

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


    public List<UserResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }


    public ResponseEntity<UserResponseDTO> getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));
        UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    public ResponseEntity<UserResponseDTO> updateUser(String id, UserResponseDTO userResponseDTO) {
       checkOwnership(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));

        user.setName(userResponseDTO.getName());
        user.setEmail(userResponseDTO.getEmail());
        user.setRole(UserRole.valueOf(userResponseDTO.getRole()));
        user.setImageUrl(userResponseDTO.getImageUrl());
        user.setPhoneNumber(userResponseDTO.getPhoneNumber());

        userRepository.save(user);
        UserResponseDTO updatedUserDTO = modelMapper.map(user, UserResponseDTO.class);
        return ResponseEntity.ok(updatedUserDTO);
    }

    public ResponseEntity<Map<String, String>> deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + id));

        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message","User successfully deleted."));
    }
}
