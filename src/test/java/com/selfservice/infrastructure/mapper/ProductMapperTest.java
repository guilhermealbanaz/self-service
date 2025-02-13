package com.selfservice.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.selfservice.application.dto.product.ProductRequestDTO;
import com.selfservice.application.dto.product.ProductResponseDTO;
import com.selfservice.domain.entity.Product;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("Deve mapear Product para ProductResponseDTO corretamente")
    void toDTO() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza Margherita");
        product.setDescription("Pizza tradicional italiana");
        product.setPrice(new BigDecimal("45.90"));

        // Act
        ProductResponseDTO dto = mapper.toDTO(product);

        // Assert
        assertNotNull(dto);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getDescription(), dto.getDescription());
        assertEquals(product.getPrice(), dto.getPrice());
    }

    @Test
    @DisplayName("Deve mapear ProductRequestDTO para Product corretamente")
    void toEntity() {
        // Arrange
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Pizza Margherita");
        dto.setDescription("Pizza tradicional italiana");
        dto.setPrice(new BigDecimal("45.90"));

        // Act
        Product product = mapper.toEntity(dto);

        // Assert
        assertNotNull(product);
        assertNull(product.getId()); // ID não deve ser mapeado do DTO
        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getPrice(), product.getPrice());
    }

    @Test
    @DisplayName("Deve atualizar Product existente com dados do ProductRequestDTO")
    void updateEntity() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Pizza Antiga");
        existingProduct.setDescription("Descrição antiga");
        existingProduct.setPrice(new BigDecimal("40.00"));

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Pizza Nova");
        dto.setDescription("Descrição nova");
        dto.setPrice(new BigDecimal("45.90"));

        // Act
        mapper.updateEntity(dto, existingProduct);

        // Assert
        assertEquals(1L, existingProduct.getId()); // ID deve permanecer o mesmo
        assertEquals(dto.getName(), existingProduct.getName());
        assertEquals(dto.getDescription(), existingProduct.getDescription());
        assertEquals(dto.getPrice(), existingProduct.getPrice());
    }

    @Test
    @DisplayName("Deve ignorar campos nulos ao atualizar Product")
    void updateEntityWithNullValues() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Pizza Original");
        existingProduct.setDescription("Descrição original");
        existingProduct.setPrice(new BigDecimal("40.00"));

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Pizza Nova");
        // Description e Price são null

        // Act
        mapper.updateEntity(dto, existingProduct);

        // Assert
        assertEquals(1L, existingProduct.getId());
        assertEquals(dto.getName(), existingProduct.getName());
        assertEquals("Descrição original", existingProduct.getDescription()); // Deve manter o valor original
        assertEquals(new BigDecimal("40.00"), existingProduct.getPrice()); // Deve manter o valor original
    }
} 