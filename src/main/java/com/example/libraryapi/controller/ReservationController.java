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
import com.example.libraryapi.mapper.ReservationMapper;
import com.example.libraryapi.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@Tag(name = "Reservations", description = "Reserve unavailable books and cancel reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @PostMapping("/reservations")
    @Operation(summary = "Reserve an unavailable book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ReservationDTO> reserveBook(@Valid @RequestBody ReservationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationMapper.toDTO(reservationService.reserveBook(request.bookId(), request.memberId())));
    }

    @GetMapping("/members/{memberId}/reservations")
    @Operation(summary = "List a member's active reservations")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<ReservationDTO> getActiveReservations(@PathVariable Long memberId) {
        return reservationService.getActiveReservations(memberId).stream().map(reservationMapper::toDTO).toList();
    }

    @DeleteMapping("/members/{memberId}/reservations/{reservationId}")
    @Operation(summary = "Cancel a member's reservation without deleting its history")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted or cancelled"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cancelReservation(@PathVariable Long memberId, @PathVariable Long reservationId) {
        reservationService.cancelReservation(memberId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
