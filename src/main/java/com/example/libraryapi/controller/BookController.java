package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        book.setId(null);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.created(URI.create("/books/" + savedBook.getId()))
                .body(savedBook);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Book book = getBookById(id);
        book.setIsbn(updatedBook.getIsbn());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setGenre(updatedBook.getGenre());
        book.setPublishedYear(updatedBook.getPublishedYear());
        book.setAvailable(updatedBook.isAvailable());
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }
}
