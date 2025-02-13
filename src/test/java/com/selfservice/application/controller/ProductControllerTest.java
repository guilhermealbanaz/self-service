package com.selfservice.application.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selfservice.application.dto.product.ProductRequestDTO;
import com.selfservice.application.dto.product.ProductResponseDTO;
import com.selfservice.domain.entity.Product;
import com.selfservice.domain.service.ProductService;
import com.selfservice.infrastructure.exception.ResourceNotFoundException;
import com.selfservice.infrastructure.mapper.ProductMapper;
import com.selfservice.infrastructure.security.CustomUserDetailsService;
import com.selfservice.infrastructure.security.JwtAuthenticationFilter;
import com.selfservice.infrastructure.security.JwtTokenProvider;
import com.selfservice.application.dto.product.ProductFilterDTO;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
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

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Pizza Margherita");
        productRequestDTO.setDescription("Pizza tradicional italiana");
        productRequestDTO.setPrice(new BigDecimal("45.90"));

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Pizza Margherita");
        productResponseDTO.setDescription("Pizza tradicional italiana");
        productResponseDTO.setPrice(new BigDecimal("45.90"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar lista de produtos com sucesso")
    void findAll() throws Exception {
        when(productService.findAll()).thenReturn(productList);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productResponseDTO);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Pizza Margherita")));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar produto por ID com sucesso")
    void findById() throws Exception {
        when(productService.findById(1L)).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productResponseDTO);

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pizza Margherita")));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 quando produto não encontrado")
    void findByIdNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve criar produto com sucesso")
    void create() throws Exception {
        when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(product);
        when(productService.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productResponseDTO);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pizza Margherita")));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 quando criar produto com dados inválidos")
    void createInvalid() throws Exception {
        ProductRequestDTO invalidProduct = new ProductRequestDTO();

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar produto com sucesso")
    void update() throws Exception {
        when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(product);
        when(productService.update(eq(1L), any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productResponseDTO);

        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pizza Margherita")));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 quando atualizar produto inexistente")
    void updateNotFound() throws Exception {
        when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(product);
        when(productService.update(eq(99L), any(Product.class)))
                .thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(put("/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve deletar produto com sucesso")
    void deleteProduct() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 quando deletar produto inexistente")
    void deleteProductNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product", 99L))
                .when(productService).delete(99L);

        mockMvc.perform(delete("/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar página de produtos com sucesso")
    void findAllPaged() throws Exception {
        ProductFilterDTO filter = new ProductFilterDTO();
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());
        when(productService.findAll(any(ProductFilterDTO.class), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productResponseDTO);

        mockMvc.perform(get("/products/paged")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "name")
                .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Pizza Margherita")));
    }
} 