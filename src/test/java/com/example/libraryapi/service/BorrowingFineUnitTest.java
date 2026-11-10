package com.example.libraryapi.service;

import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BorrowingFineUnitTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BorrowingRepository borrowingRepository;

    @InjectMocks
    private BorrowingService borrowingService;

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "On time,          2026-11-03, 0.00",
        "Exactly due,      2026-11-10, 0.00",
        "One day overdue,  2026-11-11, 0.50",
        "Seven days overdue,2026-11-17,3.50",
        "Before due date,  2026-11-09, 0.00"
    })
    void calculateFine_shouldChargeOnlyForOverdueDays(
            String scenario, LocalDate returnDate, BigDecimal expectedFine) {
        LocalDate dueDate = LocalDate.of(2026, 11, 10);

        BigDecimal fine = borrowingService.calculateFine(dueDate, returnDate);

        assertNotNull(fine);
        assertEquals(0, expectedFine.compareTo(fine));
        if (expectedFine.signum() == 0) {
            assertSame(BigDecimal.ZERO, fine);
        }
        verifyNoInteractions(bookRepository, memberRepository, borrowingRepository);
    }
}
