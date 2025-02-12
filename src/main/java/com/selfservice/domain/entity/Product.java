package com.selfservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Entity
@Schema(description = "Entity that represents a product in the system")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Product's unique ID", example = "1")
    private Long id;
    
    @NotBlank
    @Schema(description = "Product name", example = "Margherita Pizza", required = true)
    private String name;
    
    @Schema(description = "Detailed product description", example = "Traditional Italian pizza with tomato sauce, mozzarella and basil")
    private String description;
    
    @NotNull
    @Positive
    @Schema(description = "Product price", example = "45.90", required = true)
    private Double price;
} 