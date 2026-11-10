package com.example.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.math.BigDecimal;

public record BorrowingDTO(
        @Schema(description = "id", example = "1") Long id,
        @Schema(description = "book", example = "{\"id\":1,\"title\":\"Clean Code\",\"available\":false}") BookDTO book,
        @Schema(description = "member", example = "{\"id\":1,\"name\":\"Alice\",\"active\":true}") MemberDTO member,
        @Schema(description = "borrowDate", example = "2026-11-10") LocalDate borrowDate,
        @Schema(description = "dueDate", example = "2026-11-24") LocalDate dueDate,
        @Schema(description = "returnDate", example = "2026-11-20") LocalDate returnDate,
        @Schema(description = "fineAmount", example = "0.00") BigDecimal fineAmount) {
}
