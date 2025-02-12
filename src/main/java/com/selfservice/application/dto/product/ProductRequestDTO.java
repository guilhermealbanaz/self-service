package com.selfservice.application.dto.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Dados para criação/atualização de um produto")
public class ProductRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String name;
    
    @Schema(description = "Descrição detalhada do produto", example = "Pizza tradicional italiana com molho de tomate, muçarela e manjericão")
    private String description;
    
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Schema(description = "Preço do produto", example = "45.90")
    private BigDecimal price;
} 