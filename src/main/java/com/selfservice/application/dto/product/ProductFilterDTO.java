package com.selfservice.application.dto.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Critérios de filtro para busca de produtos")
public class ProductFilterDTO {
    
    @Schema(description = "Filtrar por nome do produto", example = "Pizza")
    private String name;
    
    @Schema(description = "Filtrar por descrição do produto", example = "italiana")
    private String description;
    
    @Schema(description = "Filtrar por preço mínimo", example = "20.00")
    private BigDecimal minPrice;
    
    @Schema(description = "Filtrar por preço máximo", example = "50.00")
    private BigDecimal maxPrice;
} 