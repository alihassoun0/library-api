package com.example.libraryapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

public record ErrorResponseDTO(
        @Schema(description = "Error message", example = "Book not found") String error,
        @Schema(description = "UTC timestamp", example = "2026-11-10T20:00:00Z") Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @Schema(description = "Validation errors by field", example = "{\"bookId\":\"must not be null\"}")
        Map<String, String> fieldErrors) {
}
