package com.example.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record MemberDTO(
        @Schema(description = "id", example = "1") Long id,
        @Schema(description = "name", example = "Alice") String name,
        @Schema(description = "email", example = "alice@example.com") String email,
        @Schema(description = "membershipDate", example = "2026-11-10") LocalDate membershipDate,
        @Schema(description = "active", example = "true") boolean active) {
}
