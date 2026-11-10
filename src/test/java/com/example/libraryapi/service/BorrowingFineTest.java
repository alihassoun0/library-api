package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BorrowingFineTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BorrowingService borrowingService;

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "Returned on time,         7, 0.00",
        "Exactly on due date,      0, 0.00",
        "Returned one day late,   -1, 0.50",
        "Returned seven days late,-7, 3.50",
        "Returned before due date, 1, 0.00"
    })
    void returnBook_shouldCalculateFine(
            String scenario,
            int dueDateOffset,
            BigDecimal expectedFine) {
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
        member.setName("Fine Test Member");
        member.setEmail("fine.test@example.com");
        member.setMembershipDate(today.minusDays(30));
        member.setActive(true);
        member = memberRepository.save(member);

        Borrowing borrowing = borrowingService.borrowBook(book.getId(), member.getId());
        LocalDate dueDate = today.plusDays(dueDateOffset);
        borrowing.setBorrowDate(dueDate.minusDays(14));
        borrowing.setDueDate(dueDate);
        borrowingRepository.saveAndFlush(borrowing);

        Borrowing returnedBorrowing = borrowingService.returnBook(borrowing.getId());

        assertNotNull(returnedBorrowing.getFineAmount());
        assertEquals(0, expectedFine.compareTo(returnedBorrowing.getFineAmount()));
    }
}
