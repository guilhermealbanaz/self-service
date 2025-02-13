package com.selfservice.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Dados para atualização do usuário")
public class UpdateUserRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;
    
    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @Schema(description = "Nova senha do usuário (opcional)", example = "novaSenha123")
    private String newPassword;
} 