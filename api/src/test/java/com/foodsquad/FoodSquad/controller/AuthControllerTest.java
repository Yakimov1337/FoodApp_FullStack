package com.foodsquad.FoodSquad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsquad.FoodSquad.model.dto.UserLoginDTO;
import com.foodsquad.FoodSquad.model.dto.UserRegistrationDTO;
import com.foodsquad.FoodSquad.model.dto.UserResponseDTO;
import com.foodsquad.FoodSquad.service.AuthService;
import com.foodsquad.FoodSquad.service.TokenService;
import com.foodsquad.FoodSquad.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterUser() throws Exception {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("test@example.com");
        userRegistrationDTO.setPassword("password");
        userRegistrationDTO.setConfirmPassword("password");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("test@example.com");

        when(authService.registerUser(any())).thenReturn(userResponseDTO);

        MockHttpServletResponse response = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"password\": \"password\", \"confirmPassword\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }

    @Test
    void testLoginUser() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("test@example.com");

        when(authService.loginUser(any())).thenReturn(userResponseDTO);

        MockHttpServletResponse response = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }
}
