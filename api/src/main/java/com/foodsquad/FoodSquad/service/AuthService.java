package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<String> registerUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(UserRole.NORMAL);
        user.setImageUrl("https://cloud.appwrite.io/v1/storage/buckets/66099552d89fbdfa20d4/files/663f1c48a822caaf325c/view?project=65ef1b8962547e24afec&mode=admin");

        userRepository.save(user);
        return new ResponseEntity<>("User successfully registered", HttpStatus.CREATED);
    }

    public ResponseEntity<UserResponseDTO> loginUser(UserLoginDTO userLoginDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
                return ResponseEntity.ok(modelMapper.map(user, UserResponseDTO.class));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
