package com.selfservice.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    
    @Builder.Default
    private List<ValidationError> errors = new ArrayList<>();
    
    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
    }
} 