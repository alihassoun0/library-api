package com.example.libraryapi.exception;

import com.example.libraryapi.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

final class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    static ResponseEntity<ErrorResponseDTO> create(HttpStatusCode status, String message) {
        return create(status, message, Map.of());
    }

    static ResponseEntity<ErrorResponseDTO> create(
            HttpStatusCode status, String message, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(message, Instant.now(), fieldErrors));
    }
}
