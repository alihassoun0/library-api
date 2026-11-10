package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BorrowingServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingService borrowingService;

    @Test
    void borrowBook_shouldMarkBookAsUnavailable() {
        LocalDate today = LocalDate.now();

        Book book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setGenre("Programming");
        book.setPublishedYear(2008);
        book.setAvailable(true);
        book = bookRepository.save(book);

        Member member = new Member();
        member.setName("Test Member");
        member.setEmail("test.member@example.com");
        member.setMembershipDate(today);
        member.setActive(true);
        member = memberRepository.save(member);

        Borrowing borrowing =
                borrowingService.borrowBook(book.getId(), member.getId());

        assertNotNull(borrowing.getId());

        Book updatedBook = bookRepository.findById(book.getId())
                .orElseThrow();
        assertFalse(updatedBook.isAvailable());

        assertEquals(today.plusDays(14), borrowing.getDueDate());
    }

    @Test
    void returnBook_shouldMarkBookAsAvailableAndSetZeroFine() {
        Book book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setGenre("Programming");
        book.setPublishedYear(2008);
        book.setAvailable(true);
        book = bookRepository.save(book);

        Member member = new Member();
        member.setName("Return Test Member");
        member.setEmail("return.test@example.com");
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        member = memberRepository.save(member);

        Borrowing borrowing =
                borrowingService.borrowBook(book.getId(), member.getId());

        Borrowing returnedBorrowing =
                borrowingService.returnBook(borrowing.getId());

        assertEquals(LocalDate.now(), returnedBorrowing.getReturnDate());

        Book updatedBook = bookRepository.findById(book.getId())
                .orElseThrow();
        assertTrue(updatedBook.isAvailable());

        assertNotNull(returnedBorrowing.getFineAmount());
        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(returnedBorrowing.getFineAmount())
        );
    }
}
