package com.selfservice.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Dados para login do usuário")
public class LoginRequestDTO {
    
    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @NotBlank(message = "A senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "senha123")
    private String password;
} 