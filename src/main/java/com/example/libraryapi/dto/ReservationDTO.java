package com.example.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ReservationDTO(
        @Schema(description = "id", example = "1") Long id,
        @Schema(description = "book", example = "{\"id\":1,\"available\":false}") BookDTO book,
        @Schema(description = "member", example = "{\"id\":1,\"active\":true}") MemberDTO member,
        @Schema(description = "reservationDate", example = "2026-11-10") LocalDate reservationDate,
        @Schema(description = "active", example = "true") boolean active) {
}
