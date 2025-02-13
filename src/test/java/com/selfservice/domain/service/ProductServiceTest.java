package com.selfservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.selfservice.application.dto.product.ProductFilterDTO;
import com.selfservice.domain.entity.Product;
import com.selfservice.infrastructure.exception.ResourceNotFoundException;
import com.selfservice.infrastructure.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Pizza Margherita");
        product.setDescription("Pizza tradicional italiana");
        product.setPrice(new BigDecimal("45.90"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Pizza Calabresa");
        product2.setDescription("Pizza com calabresa");
        product2.setPrice(new BigDecimal("40.90"));

        productList = Arrays.asList(product, product2);
    }

    @Test
    @DisplayName("Deve retornar uma página de produtos quando buscar todos com paginação")
    void findAllPaged() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(productList, result.getContent());
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar uma página de produtos quando buscar com filtros")
    void findAllWithFilter() {
        // Arrange
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setName("Pizza");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findAll(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(productList, result.getContent());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar lista de produtos quando buscar todos")
    void findAll() {
        // Arrange
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(productList, result);
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar um produto quando buscar por ID existente")
    void findByIdExisting() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando buscar por ID inexistente")
    void findByIdNonExisting() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
        verify(productRepository).findById(99L);
    }

    @Test
    @DisplayName("Deve salvar um produto com sucesso")
    void save() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.save(product);

        // Assert
        assertNotNull(result);
        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Deve atualizar um produto existente com sucesso")
    void updateExisting() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Pizza Margherita Atualizada");
        updatedProduct.setPrice(new BigDecimal("49.90"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.update(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProduct, result);
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar produto inexistente")
    void updateNonExisting() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.update(99L, product));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve deletar um produto existente com sucesso")
    void deleteExisting() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.delete(1L);

        // Assert
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar produto inexistente")
    void deleteNonExisting() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(99L));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).delete(any(Product.class));
    }
} 