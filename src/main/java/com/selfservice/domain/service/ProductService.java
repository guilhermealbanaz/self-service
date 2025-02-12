package com.selfservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.selfservice.domain.entity.Product;
import com.selfservice.infrastructure.exception.ResourceNotFoundException;
import com.selfservice.infrastructure.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        Product existingProduct = findById(id);
        product.setId(existingProduct.getId());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
} 