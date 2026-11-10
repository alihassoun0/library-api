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
import com.example.libraryapi.mapper.BookMapper;
import com.example.libraryapi.service.BookService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Manage library books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    @Operation(summary = "List all books")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks().stream().map(bookMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public BookDTO getBookById(@PathVariable Long id) {
        return bookMapper.toDTO(bookService.getBookById(id));
    }

    @PostMapping
    @Operation(summary = "Create a book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO book) {
        BookDTO saved = bookMapper.toDTO(bookService.createBook(bookMapper.toEntity(book)));
        return ResponseEntity.created(URI.create("/books/" + saved.id())).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a book's fields")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO updatedBook) {
        return bookMapper.toDTO(bookService.updateBook(id, bookMapper.toEntity(updatedBook)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted or cancelled"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
