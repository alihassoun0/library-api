package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceUnitTest {
    @Mock private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private BorrowingRepository borrowingRepository;
    @InjectMocks private BorrowingService service;

    private Book book;
    private Member member;
    private static final LocalDate DUE = LocalDate.of(2026, 11, 10);

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(10L);
        book.setAvailable(true);
        member = new Member();
        member.setId(20L);
        member.setActive(true);
    }

    @Test
    void borrowAvailableBook_shouldPersistRelationshipsDatesAndAvailability() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(20L)).thenReturn(Optional.of(member));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(call -> call.getArgument(0));
        LocalDate today = LocalDate.now();

        Borrowing saved = service.borrowBook(10L, 20L);

        assertSame(book, saved.getBook());
        assertSame(member, saved.getMember());
        assertEquals(today, saved.getBorrowDate());
        assertEquals(today.plusDays(14), saved.getDueDate());
        assertFalse(book.isAvailable());
        assertNull(saved.getReturnDate());
        verify(bookRepository).save(book);
        verify(borrowingRepository).save(saved);
    }

    @Test
    void borrowMissingBook_shouldReturn404() {
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.borrowBook(10L, 20L));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        assertEquals("Book not found", error.getReason());
        verifyNoInteractions(memberRepository, borrowingRepository);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void borrowMissingMember_shouldReturn404() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(20L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.borrowBook(10L, 20L));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        assertEquals("Member not found", error.getReason());
        assertTrue(book.isAvailable());
        verify(bookRepository, never()).save(any(Book.class));
        verifyNoInteractions(borrowingRepository);
    }

    @Test
    void borrowUnavailableBook_shouldRejectConflict() {
        book.setAvailable(false);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(20L)).thenReturn(Optional.of(member));
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.borrowBook(10L, 20L));
        assertEquals("Book is unavailable", error.getMessage());
        assertFalse(book.isAvailable());
        verify(bookRepository, never()).save(any(Book.class));
        verifyNoInteractions(borrowingRepository);
    }

    @Test
    void returnOnTime_shouldRecordZeroFineAndRestoreAvailability() {
        Borrowing loan = stubOutstandingLoan(LocalDate.now().plusDays(7));
        Borrowing saved = service.returnBook(30L);
        assertSame(loan, saved);
        assertMoney("0.00", saved.getFineAmount());
        assertEquals(LocalDate.now(), saved.getReturnDate());
        assertTrue(book.isAvailable());
        verify(bookRepository).save(book);
        verify(borrowingRepository).save(loan);
    }

    @Test
    void returnThreeDaysLate_shouldChargeOneFifty() {
        stubOutstandingLoan(LocalDate.now().minusDays(3));
        Borrowing saved = service.returnBook(30L);
        assertMoney("1.50", saved.getFineAmount());
        assertEquals(LocalDate.now(), saved.getReturnDate());
        assertTrue(book.isAvailable());
        verify(borrowingRepository).save(saved);
    }

    @Test
    void returnMissingBorrowing_shouldReturn404() {
        when(borrowingRepository.findById(30L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.returnBook(30L));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        assertEquals("Borrowing not found", error.getReason());
        verifyNoInteractions(bookRepository, memberRepository);
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void returnExactlyDue_shouldChargeZero() {
        stubOutstandingLoan(LocalDate.now());
        Borrowing saved = service.returnBook(30L);
        assertMoney("0.00", saved.getFineAmount());
        assertEquals(LocalDate.now(), saved.getReturnDate());
        assertTrue(book.isAvailable());
    }

    @Test
    void returnAlreadyReturned_shouldRejectWithoutChangingStateOrSaving() {
        Borrowing loan = new Borrowing();
        loan.setBook(book);
        loan.setReturnDate(DUE);
        loan.setFineAmount(new BigDecimal("2.00"));
        when(borrowingRepository.findById(30L)).thenReturn(Optional.of(loan));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.returnBook(30L));

        assertEquals("Book has already been returned", error.getMessage());
        assertEquals(DUE, loan.getReturnDate());
        assertMoney("2.00", loan.getFineAmount());
        assertTrue(book.isAvailable());
        verifyNoInteractions(bookRepository);
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void fineZeroDaysLate_shouldBeZero() {
        assertMoney("0.00", service.calculateFine(DUE, DUE));
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }

    @Test
    void fineOneDayLate_shouldBeFiftyCents() {
        assertMoney("0.50", service.calculateFine(DUE, DUE.plusDays(1)));
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }

    @Test
    void fineSevenDaysLate_shouldBeThreeFifty() {
        assertMoney("3.50", service.calculateFine(DUE, DUE.plusDays(7)));
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }

    @Test
    void fineNegativeDays_shouldBeZero() {
        assertMoney("0.00", service.calculateFine(DUE, DUE.minusDays(1)));
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }

    @Test
    void fineAcrossMonthBoundary_shouldCountElapsedDays() {
        assertMoney("0.50", service.calculateFine(LocalDate.of(2026, 1, 31), LocalDate.of(2026, 2, 1)));
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }

    @Test
    void activeBorrowings_shouldReturnRepositoryResultsForMember() {
        Borrowing loan = new Borrowing();
        loan.setId(30L);
        when(memberRepository.existsById(20L)).thenReturn(true);
        when(borrowingRepository.findByMemberIdAndReturnDateIsNull(20L)).thenReturn(List.of(loan));
        assertEquals(List.of(loan), service.getActiveBorrowings(20L));
        verify(borrowingRepository).findByMemberIdAndReturnDateIsNull(20L);
    }

    @Test
    void activeBorrowingsMissingMember_shouldReturn404() {
        when(memberRepository.existsById(20L)).thenReturn(false);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.getActiveBorrowings(20L));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(borrowingRepository);
    }

    @Test
    void activeBorrowingsExistingMemberWithoutLoans_shouldReturnEmptyList() {
        when(memberRepository.existsById(20L)).thenReturn(true);
        when(borrowingRepository.findByMemberIdAndReturnDateIsNull(20L)).thenReturn(List.of());

        assertEquals(List.of(), service.getActiveBorrowings(20L));
        verify(borrowingRepository).findByMemberIdAndReturnDateIsNull(20L);
    }

    private Borrowing stubOutstandingLoan(LocalDate dueDate) {
        book.setAvailable(false);
        Borrowing loan = new Borrowing();
        loan.setId(30L);
        loan.setBook(book);
        loan.setMember(member);
        loan.setDueDate(dueDate);
        when(borrowingRepository.findById(30L)).thenReturn(Optional.of(loan));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(call -> call.getArgument(0));
        return loan;
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
