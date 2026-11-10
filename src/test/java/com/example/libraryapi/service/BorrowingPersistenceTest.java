package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BorrowingPersistenceTest {

    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void returnBook_shouldPersistReturnAndAvailabilityAfterClearingContext() {
        Book book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setAvailable(true);
        book = bookRepository.save(book);

        Member member = new Member();
        member.setName("Persistence Test Member");
        member.setEmail("persistence@example.com");
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        member = memberRepository.save(member);

        Borrowing borrowing = borrowingService.borrowBook(book.getId(), member.getId());
        entityManager.flush();
        entityManager.clear();
        assertFalse(bookRepository.findById(book.getId()).orElseThrow().isAvailable());

        borrowingService.returnBook(borrowing.getId());
        entityManager.flush();
        entityManager.clear();

        Borrowing saved = borrowingRepository.findById(borrowing.getId()).orElseThrow();
        assertEquals(LocalDate.now(), saved.getReturnDate());
        assertNotNull(saved.getFineAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getFineAmount()));
        assertTrue(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }
}
