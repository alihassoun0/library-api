package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.service.BorrowingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    public record BorrowingRequest(Long bookId, Long memberId) {
    }
}
