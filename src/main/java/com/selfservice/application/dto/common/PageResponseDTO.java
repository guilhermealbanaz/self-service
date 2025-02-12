package com.selfservice.application.dto.common;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada genérica")
public class PageResponseDTO<T> {
    
    @Schema(description = "Lista de itens da página atual")
    private List<T> content;
    
    @Schema(description = "Número da página atual (começa em 0)", example = "0")
    private int pageNumber;
    
    @Schema(description = "Tamanho da página", example = "10")
    private int pageSize;
    
    @Schema(description = "Total de elementos", example = "100")
    private long totalElements;
    
    @Schema(description = "Total de páginas", example = "10")
    private int totalPages;
    
    @Schema(description = "Indica se é a primeira página", example = "true")
    private boolean first;
    
    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;
} 