package com.selfservice.application.dto.auth;

import java.util.Set;

import com.selfservice.domain.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta após autenticação bem-sucedida")
public class AuthResponseDTO {
    
    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Tipo do token", example = "Bearer")
    private String type;
    
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;
    
    @Schema(description = "Papéis do usuário", example = "[\"CUSTOMER\"]")
    private Set<Role> roles;
} 