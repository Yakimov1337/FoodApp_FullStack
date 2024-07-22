package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.Token;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.TokenRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(UserRole.NORMAL);
        user.setImageUrl("https://cloud.appwrite.io/v1/storage/buckets/66099552d89fbdfa20d4/files/663f1c48a822caaf325c/view?project=65ef1b8962547e24afec&mode=admin");

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Token> token = tokenRepository.findByUserAndToken(user, refreshToken);
        return token.isPresent();
    }

    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //End all other sessions
        tokenRepository.deleteByUser(user);

        Token token = new Token();
        token.setToken(refreshToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        token.setUser(user);

        tokenRepository.save(token);
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        tokenRepository.deleteByToken(refreshToken);
    }

}
