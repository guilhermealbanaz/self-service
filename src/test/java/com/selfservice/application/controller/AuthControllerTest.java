package com.selfservice.application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selfservice.application.dto.auth.AuthResponseDTO;
import com.selfservice.application.dto.auth.LoginRequestDTO;
import com.selfservice.application.dto.auth.RegisterRequestDTO;
import com.selfservice.application.dto.auth.UpdateUserRequestDTO;
import com.selfservice.domain.entity.Role;
import com.selfservice.domain.service.AuthService;
import com.selfservice.infrastructure.exception.BusinessException;
import com.selfservice.infrastructure.exception.ResourceNotFoundException;
import com.selfservice.infrastructure.security.CustomUserDetailsService;
import com.selfservice.infrastructure.security.JwtAuthenticationFilter;
import com.selfservice.infrastructure.security.JwtTokenProvider;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private UpdateUserRequestDTO updateRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        updateRequest = new UpdateUserRequestDTO();
        updateRequest.setName("Updated User");
        updateRequest.setEmail("updated@test.com");
        updateRequest.setNewPassword("newpassword123");

        authResponse = AuthResponseDTO.builder()
                .token("jwt.token.test")
                .type("Bearer")
                .email("test@test.com")
                .name("Test User")
                .roles(Set.of(Role.CUSTOMER))
                .build();
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registerSuccess() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.type").value(authResponse.getType()))
                .andExpect(jsonPath("$.email").value(authResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(authResponse.getName()))
                .andExpect(jsonPath("$.roles[0]").value(Role.CUSTOMER.name()));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar registrar com dados inválidos")
    void registerInvalidData() throws Exception {
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void loginSuccess() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.type").value(authResponse.getType()))
                .andExpect(jsonPath("$.email").value(authResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(authResponse.getName()))
                .andExpect(jsonPath("$.roles[0]").value(Role.CUSTOMER.name()));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar login com dados inválidos")
    void loginInvalidData() throws Exception {
        LoginRequestDTO invalidRequest = new LoginRequestDTO();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar usuário com sucesso")
    void updateUserSuccess() throws Exception {
        when(authService.updateUser(any(Long.class), any(UpdateUserRequestDTO.class)))
                .thenReturn(authResponse);

        mockMvc.perform(put("/auth/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.type").value(authResponse.getType()))
                .andExpect(jsonPath("$.email").value(authResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(authResponse.getName()))
                .andExpect(jsonPath("$.roles[0]").value(Role.CUSTOMER.name()));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar erro ao tentar atualizar usuário inexistente")
    void updateUserNotFound() throws Exception {
        when(authService.updateUser(any(Long.class), any(UpdateUserRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("User", 1L));

        mockMvc.perform(put("/auth/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar erro ao tentar atualizar com email já existente")
    void updateUserEmailExists() throws Exception {
        when(authService.updateUser(any(Long.class), any(UpdateUserRequestDTO.class)))
                .thenThrow(new BusinessException("Email já cadastrado"));

        mockMvc.perform(put("/auth/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
} 