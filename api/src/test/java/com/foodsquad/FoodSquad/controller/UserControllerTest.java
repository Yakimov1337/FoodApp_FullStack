package com.foodsquad.FoodSquad.controller;

import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.model.dto.UserUpdateDTO;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser() throws Exception {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        when(authService.registerUser(any(UserRegistrationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"password\": \"password\", \"confirmPassword\": \"password\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userResponseDTO.getEmail())));

        verify(authService, times(1)).registerUser(any(UserRegistrationDTO.class));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        when(userService.getAllUsers(0, 10)).thenReturn(Collections.singletonList(userResponseDTO));

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is(userResponseDTO.getEmail())));

        verify(userService, times(1)).getAllUsers(0, 10);
    }

    @Test
    void testGetUserById() throws Exception {
        String userId = "1";
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        when(userService.getUserById(userId)).thenReturn(ResponseEntity.ok(userResponseDTO));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userResponseDTO.getEmail())));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testUpdateUser() throws Exception {
        String userId = "1";
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setName("Updated Name");
        userUpdateDTO.setRole("NORMAL");
        userUpdateDTO.setImageUrl("http://example.com/image.jpg");
        userUpdateDTO.setPhoneNumber("+359 899 78 7878");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setName("Updated Name");
        userResponseDTO.setRole("NORMAL");
        userResponseDTO.setEmail("test@example.com");

        when(userService.updateUser(eq(userId), any(UserUpdateDTO.class))).thenReturn(ResponseEntity.ok(userResponseDTO));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Updated Name\", \"role\": \"NORMAL\", \"imageUrl\": \"http://example.com/image.jpg\", \"phoneNumber\": \"+359 899 78 7878\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userResponseDTO.getName())))
                .andExpect(jsonPath("$.role", is(userResponseDTO.getRole())))
                .andExpect(jsonPath("$.email", is(userResponseDTO.getEmail())));

        verify(userService, times(1)).updateUser(eq(userId), any(UserUpdateDTO.class));
    }


    @Test
    void testDeleteUser() throws Exception {
        String userId = "1";

        when(userService.deleteUser(userId)).thenReturn(ResponseEntity.ok(Map.of("message", "User successfully deleted")));

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User successfully deleted")));

        verify(userService, times(1)).deleteUser(userId);
    }
}
