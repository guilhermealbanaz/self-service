package com.selfservice.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;
    
    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Senha do usuário", example = "senha123")
    private String password;
} 