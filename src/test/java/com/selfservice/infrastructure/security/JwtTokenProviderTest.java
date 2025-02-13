package com.selfservice.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.selfservice.domain.entity.Role;
import com.selfservice.domain.entity.User;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private User user;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 3600000); // 1 hora
        tokenProvider.init(); // Inicializa a chave

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .password("password123")
                .roles(Set.of(Role.CUSTOMER))
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void generateToken() {
        // Act
        String token = tokenProvider.generateToken(user);

        // Assert
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(user.getEmail(), tokenProvider.getEmailFromToken(token));
    }

    @Test
    @DisplayName("Deve validar token JWT")
    void validateToken() {
        // Arrange
        String token = tokenProvider.generateToken(user);

        // Act & Assert
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Deve extrair email do token JWT")
    void getEmailFromToken() {
        // Arrange
        String token = tokenProvider.generateToken(user);

        // Act
        String email = tokenProvider.getEmailFromToken(token);

        // Assert
        assertEquals(user.getEmail(), email);
    }

    @Test
    @DisplayName("Deve retornar falso para token inválido")
    void validateInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertFalse(tokenProvider.validateToken(invalidToken));
    }
} 