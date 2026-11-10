package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.service.BorrowingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping
    public ResponseEntity<Borrowing> borrowBook(@RequestBody BorrowingRequest request) {
        Borrowing borrowing = borrowingService.borrowBook(request.bookId(), request.memberId());
        return ResponseEntity.created(URI.create("/borrowings/" + borrowing.getId()))
                .body(borrowing);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Borrowing> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    public record BorrowingRequest(Long bookId, Long memberId) {
    }
}
