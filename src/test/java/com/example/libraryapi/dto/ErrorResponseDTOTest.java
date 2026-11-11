package com.example.libraryapi.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorResponseDTOTest {

    @Test
    void constructor_shouldSnapshotInputWithoutChangingOrderOrNullValues() {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("bookId", "must not be null");
        input.put("memberId", null);
        ErrorResponseDTO response = new ErrorResponseDTO("Validation failed", Instant.EPOCH, input);

        input.put("bookId", "changed later");
        input.remove("memberId");

        assertEquals(List.of("bookId", "memberId"), List.copyOf(response.fieldErrors().keySet()));
        assertEquals("must not be null", response.fieldErrors().get("bookId"));
        assertNull(response.fieldErrors().get("memberId"));
    }

    @Test
    void accessor_shouldPreventMutationOfTheResponseSnapshot() {
        ErrorResponseDTO response = new ErrorResponseDTO("Validation failed", Instant.EPOCH,
                Map.of("bookId", "must not be null"));

        assertThrows(UnsupportedOperationException.class,
                () -> response.fieldErrors().put("bookId", "changed later"));
        assertEquals("must not be null", response.fieldErrors().get("bookId"));
    }

    @Test
    void constructor_shouldPreserveOptionalNullFieldErrors() {
        ErrorResponseDTO response = new ErrorResponseDTO("Not found", Instant.EPOCH, null);

        assertNull(response.fieldErrors());
    }
}
