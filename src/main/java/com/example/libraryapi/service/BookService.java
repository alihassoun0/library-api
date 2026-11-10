package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @Transactional
    public Book createBook(Book book) {
        book.setId(null);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        Book book = getBookById(id);
        book.setIsbn(updatedBook.getIsbn());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setGenre(updatedBook.getGenre());
        book.setPublishedYear(updatedBook.getPublishedYear());
        book.setAvailable(updatedBook.isAvailable());
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.delete(getBookById(id));
    }
}
