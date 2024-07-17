package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserResponseDTO.class);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public UserResponseDTO updateUser(Long id, UserResponseDTO userResponseDTO) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userResponseDTO.getName());
            user.setEmail(userResponseDTO.getEmail());
            user.setRole(UserRole.valueOf(userResponseDTO.getRole()));
            user.setImageUrl(userResponseDTO.getImageUrl());
            user.setPhoneNumber(userResponseDTO.getPhoneNumber());
            User updatedUser = userRepository.save(user);
            return modelMapper.map(updatedUser, UserResponseDTO.class);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

}

