package com.selfservice.application.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.selfservice.application.dto.common.PageResponseDTO;
import com.selfservice.application.dto.product.ProductFilterDTO;
import com.selfservice.application.dto.product.ProductRequestDTO;
import com.selfservice.application.dto.product.ProductResponseDTO;
import com.selfservice.domain.entity.Product;
import com.selfservice.domain.service.ProductService;
import com.selfservice.infrastructure.mapper.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @GetMapping("/paged")
    @Operation(summary = "List products with pagination and filters", description = "Returns a filtered and paginated list of products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered and paginated list of products")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> findAllPaged(
            @Parameter(description = "Filtrar por nome do produto")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Filtrar por descrição do produto")
            @RequestParam(required = false) String description,
            
            @Parameter(description = "Filtrar por preço mínimo")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Filtrar por preço máximo")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação", example = "name")
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {
        
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setName(name);
        filter.setDescription(description);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Product> productPage = productService.findAll(filter, pageable);
        
        return ResponseEntity.ok(
            PageResponseDTO.<ProductResponseDTO>builder()
                .content(productPage.getContent().stream()
                    .map(productMapper::toDTO)
                    .toList())
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build()
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