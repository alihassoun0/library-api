package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.service.BorrowingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/borrowings")
    public ResponseEntity<Borrowing> borrowBook(@RequestBody BorrowingRequest request) {
        Borrowing borrowing = borrowingService.borrowBook(request.bookId(), request.memberId());
        return ResponseEntity.created(URI.create("/borrowings/" + borrowing.getId()))
                .body(borrowing);
    }

    @PutMapping("/borrowings/{id}/return")
    public ResponseEntity<Borrowing> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    @GetMapping("/members/{id}/borrowings")
    public List<Borrowing> getActiveBorrowings(@PathVariable Long id) {
        return borrowingService.getActiveBorrowings(id);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOperation(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", exception.getMessage()));
    }

    public record BorrowingRequest(Long bookId, Long memberId) {
    }
}
