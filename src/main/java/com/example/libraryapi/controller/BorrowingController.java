package com.example.libraryapi.controller;

import com.example.libraryapi.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.libraryapi.mapper.BorrowingMapper;
import com.example.libraryapi.service.BorrowingService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Borrowings", description = "Borrow, return and list active loans")
public class BorrowingController {
    private final BorrowingService borrowingService;
    private final BorrowingMapper borrowingMapper;

    public BorrowingController(BorrowingService borrowingService, BorrowingMapper borrowingMapper) {
        this.borrowingService = borrowingService;
        this.borrowingMapper = borrowingMapper;
    }

    @PostMapping("/borrowings")
    @Operation(summary = "Borrow an available book for 14 days")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<BorrowingDTO> borrowBook(@Valid @RequestBody BorrowingRequestDTO request) {
        BorrowingDTO saved = borrowingMapper.toDTO(borrowingService.borrowBook(request.bookId(), request.memberId()));
        return ResponseEntity.created(URI.create("/borrowings/" + saved.id())).body(saved);
    }

    @PutMapping("/borrowings/{id}/return")
    @Operation(summary = "Return a book and calculate its overdue fine")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<BorrowingDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingMapper.toDTO(borrowingService.returnBook(id)));
    }

    @GetMapping("/members/{id}/borrowings")
    @Operation(summary = "List a member's active borrowings")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<BorrowingDTO> getActiveBorrowings(@PathVariable Long id) {
        return borrowingService.getActiveBorrowings(id).stream().map(borrowingMapper::toDTO).toList();
    }
}
