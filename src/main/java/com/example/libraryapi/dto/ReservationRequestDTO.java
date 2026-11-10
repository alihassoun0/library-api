package com.example.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestDTO(
        @Schema(description = "bookId", example = "1") @NotNull Long bookId,
        @Schema(description = "memberId", example = "1") @NotNull Long memberId) {
}
