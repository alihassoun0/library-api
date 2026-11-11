package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowingService {

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");
    private static final int DEFAULT_LOAN_DAYS = 14;

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;

    public BorrowingService(BookRepository bookRepository,
                            MemberRepository memberRepository,
                            BorrowingRepository borrowingRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowingRepository = borrowingRepository;
    }

    @Transactional
    public Borrowing borrowBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is unavailable");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(today);
        borrowing.setDueDate(today.plusDays(DEFAULT_LOAN_DAYS));

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing not found"));

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException("Book has already been returned");
        }

        applyReturnState(borrowing);
        return borrowingRepository.save(borrowing);
    }

    private void applyReturnState(Borrowing borrowing) {
        LocalDate today = LocalDate.now();
        BigDecimal fine = calculateFine(borrowing.getDueDate(), today);
        borrowing.setReturnDate(today);
        borrowing.setFineAmount(fine);

        Book book = borrowing.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Borrowing> getActiveBorrowings(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        return borrowingRepository.findByMemberIdAndReturnDateIsNull(memberId);
    }

    public BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return calculateFine(overdueDays);
    }

    private BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
    }
}
