package com.selfservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.selfservice.application.dto.auth.AuthResponseDTO;
import com.selfservice.application.dto.auth.LoginRequestDTO;
import com.selfservice.application.dto.auth.RegisterRequestDTO;
import com.selfservice.application.dto.auth.UpdateUserRequestDTO;
import com.selfservice.domain.entity.Role;
import com.selfservice.domain.entity.User;
import com.selfservice.infrastructure.exception.BusinessException;
import com.selfservice.infrastructure.exception.ResourceNotFoundException;
import com.selfservice.infrastructure.repository.UserRepository;
import com.selfservice.infrastructure.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private UpdateUserRequestDTO updateRequest;
    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        // Configurando dados de teste
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

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .password("encodedPassword")
                .roles(Set.of(Role.CUSTOMER))
                .enabled(true)
                .build();

        token = "jwt.token.test";
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registerSuccess() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any(User.class))).thenReturn(token);

        // Act
        AuthResponseDTO response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getRoles(), response.getRoles());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(tokenProvider).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar com email já existente")
    void registerEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void loginSuccess() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(User.class))).thenReturn(token);

        // Act
        AuthResponseDTO response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getRoles(), response.getRoles());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(user);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void updateUserSuccess() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any(User.class))).thenReturn(token);

        // Act
        AuthResponseDTO response = authService.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getName(), response.getName());

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail(updateRequest.getEmail());
        verify(passwordEncoder).encode(updateRequest.getNewPassword());
        verify(userRepository).save(any(User.class));
        verify(tokenProvider).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void updateUserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com email já existente")
    void updateUserEmailExists() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> authService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail(updateRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
} 