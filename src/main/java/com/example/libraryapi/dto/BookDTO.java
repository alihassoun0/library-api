package com.example.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookDTO(
        @Schema(description = "id", example = "1") Long id,
        @Schema(description = "isbn", example = "9780132350884") String isbn,
        @Schema(description = "title", example = "Clean Code") String title,
        @Schema(description = "author", example = "Robert C. Martin") String author,
        @Schema(description = "genre", example = "Programming") String genre,
        @Schema(description = "publishedYear", example = "2008") Integer publishedYear,
        @Schema(description = "available", example = "true") boolean available) {
}
