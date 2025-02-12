package com.selfservice.application.dto.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados de retorno de um produto")
public class ProductResponseDTO {
    
    @Schema(description = "ID único do produto", example = "1")
    private Long id;
    
    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String name;
    
    @Schema(description = "Descrição detalhada do produto", example = "Pizza tradicional italiana com molho de tomate, muçarela e manjericão")
    private String description;
    
    @Schema(description = "Preço do produto", example = "45.90")
    private BigDecimal price;
} 