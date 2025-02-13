package com.selfservice.domain.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.CUSTOMER))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles())
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = tokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public AuthResponseDTO updateUser(Long userId, UpdateUserRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Verifica se o novo email já está em uso por outro usuário
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles())
                .build();
    }
} 