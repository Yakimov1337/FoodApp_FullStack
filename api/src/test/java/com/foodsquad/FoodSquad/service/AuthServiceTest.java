package com.foodsquad.FoodSquad.service;

import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.entity.User;
import com.foodsquad.FoodSquad.model.entity.UserRole;
import com.foodsquad.FoodSquad.repository.TokenRepository;
import com.foodsquad.FoodSquad.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = authService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(email));
    }

    @Test
    void testLoadUserEntityByUsername_UserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User userDetails = authService.loadUserEntityByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getEmail());
    }

    @Test
    void testLoadUserEntityByUsername_UserDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserEntityByUsername(email));
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("test@example.com");
        userRegistrationDTO.setPassword("password");
        userRegistrationDTO.setConfirmPassword("password");

        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword("encodedPassword");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("test@example.com");

        when(userRepository.findByEmail(userRegistrationDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRegistrationDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO response = authService.registerUser(userRegistrationDTO);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("test@example.com");
        userRegistrationDTO.setPassword("password");
        userRegistrationDTO.setConfirmPassword("password");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");

        when(userRepository.findByEmail(userRegistrationDTO.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(userRegistrationDTO));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("test@example.com");

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO response = authService.loginUser(userLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testLoginUser_InvalidEmailOrPassword() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(userLoginDTO));
    }

    @Test
    void testLoginUser_InvalidPassword() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(userLoginDTO));
    }
}
