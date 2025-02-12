package com.selfservice.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selfservice.application.dto.product.ProductRequestDTO;
import com.selfservice.application.dto.product.ProductResponseDTO;
import com.selfservice.domain.service.ProductService;
import com.selfservice.infrastructure.mapper.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API for product management")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "List all products", description = "Returns a list of all products in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products")
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(
            productService.findAll().stream()
                .map(productMapper::toDTO)
                .toList()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID", description = "Returns a single product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
            productMapper.toDTO(productService.findById(id))
        );
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toDTO(
                    productService.save(productMapper.toEntity(productDTO))
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequestDTO productDTO) {
        return ResponseEntity.ok(
            productMapper.toDTO(
                productService.update(id, productMapper.toEntity(productDTO))
            )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product from the system by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 