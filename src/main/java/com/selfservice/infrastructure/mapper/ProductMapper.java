package com.selfservice.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.selfservice.application.dto.product.ProductRequestDTO;
import com.selfservice.application.dto.product.ProductResponseDTO;
import com.selfservice.domain.entity.Product;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {
    
    ProductResponseDTO toDTO(Product product);
    
    Product toEntity(ProductRequestDTO dto);
    
    void updateEntity(ProductRequestDTO dto, @MappingTarget Product product);
} 