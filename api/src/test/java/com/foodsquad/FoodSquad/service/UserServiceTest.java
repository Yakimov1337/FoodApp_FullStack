package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.dto.UserUpdateDTO;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext("test@example.com");
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user = new User();
        user.setId("1");
        user.setEmail("test@example.com");
        user.setRole(UserRole.NORMAL);

        Page<User> userPage = mock(Page.class);
        when(userPage.stream()).thenReturn(Arrays.asList(user).stream());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(orderRepository.countByUserId("1")).thenReturn(10L);

        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        userResponseDTO.setOrdersCount(10);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(userResponseDTO);

        // Act
        List<UserResponseDTO> users = userService.getAllUsers(0, 10);

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
        assertEquals(10, users.get(0).getOrdersCount());
    }

    @Test
    void testGetUserById() {
        // Arrange
        String userId = "1";
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.countByUserId(userId)).thenReturn(10L);

        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        userResponseDTO.setOrdersCount(10);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(userResponseDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userService.getUserById(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals(10, response.getBody().getOrdersCount());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        String userId = "1";
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setName("Updated Name");
        userUpdateDTO.setRole("NORMAL");
        userUpdateDTO.setImageUrl("http://example.com/image.jpg");
        userUpdateDTO.setPhoneNumber("+359 899 78 7878");

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setRole(UserRole.NORMAL);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));

        User user = new User();
        user.setId(userId);
        user.setName("Default Name");
        user.setImageUrl("https://www.pngarts.com/files/11/Avatar-Transparent-Images.png");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setName(userUpdateDTO.getName());
            savedUser.setImageUrl(userUpdateDTO.getImageUrl());
            savedUser.setPhoneNumber(userUpdateDTO.getPhoneNumber());
            return savedUser;
        });

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setImageUrl("http://example.com/image.jpg");
        updatedUser.setPhoneNumber("+359 899 78 7878");

        UserResponseDTO updatedUserDTO = new UserResponseDTO(updatedUser);
        updatedUserDTO.setOrdersCount(10);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(updatedUserDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userService.updateUser(userId, userUpdateDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("http://example.com/image.jpg", response.getBody().getImageUrl());
    }

    @Test
    void testDeleteUser() {
        // Arrange
        String userId = "1";
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.NORMAL);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, String>> response = userService.deleteUser(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("User successfully deleted.", response.getBody().get("message"));
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_AdminUser() {
        // Arrange
        String userId = "1";
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("Admin users cannot be deleted.", exception.getMessage());
        verify(userRepository, times(0)).delete(user);
    }

    private void mockSecurityContext(String email) {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", Collections.emptyList()));
    }

    // Additional tests for other methods can be added here
}
