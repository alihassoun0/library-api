package com.example.libraryapi.controller;

import com.example.libraryapi.dto.BookDTO;
import com.example.libraryapi.dto.BorrowingDTO;
import com.example.libraryapi.dto.ErrorResponseDTO;
import com.example.libraryapi.dto.MemberDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BorrowingHttpIntegrationTest {
    @Autowired private TestRestTemplate http;

    @Test
    void createAndBorrow_shouldExposeUnavailableBookThroughRealHttp() {
        Fixture fixture = createFixture();
        BorrowingDTO loan = borrow(fixture);

        assertEquals(fixture.bookId(), loan.book().id());
        assertEquals(fixture.memberId(), loan.member().id());
        ResponseEntity<BookDTO> read = http.getForEntity("/books/{id}", BookDTO.class, fixture.bookId());
        assertEquals(HttpStatus.OK, read.getStatusCode());
        assertNotNull(read.getBody());
        assertEquals(fixture.bookId(), read.getBody().id());
        assertFalse(read.getBody().available());
    }

    @Test
    void borrowAndReturn_shouldRestoreAvailabilityAndPersistZeroFineThroughRealHttp() {
        Fixture fixture = createFixture();
        BorrowingDTO loan = borrow(fixture);
        ResponseEntity<BorrowingDTO> returned = http.exchange("/borrowings/{id}/return", HttpMethod.PUT,
                HttpEntity.EMPTY, BorrowingDTO.class, loan.id());

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertNotNull(returned.getBody());
        assertEquals(loan.id(), returned.getBody().id());
        assertNotNull(returned.getBody().returnDate());
        assertNotNull(returned.getBody().fineAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(returned.getBody().fineAmount()));
        ResponseEntity<BookDTO> read = http.getForEntity("/books/{id}", BookDTO.class, fixture.bookId());
        assertEquals(HttpStatus.OK, read.getStatusCode());
        assertNotNull(read.getBody());
        assertTrue(read.getBody().available());
        ResponseEntity<BorrowingDTO[]> active = http.getForEntity("/members/{id}/borrowings",
                BorrowingDTO[].class, fixture.memberId());
        assertEquals(HttpStatus.OK, active.getStatusCode());
        assertNotNull(active.getBody());
        assertEquals(0, active.getBody().length);
    }

    @Test
    void secondBorrow_shouldReturn409WithoutCreatingAnotherLoanThroughRealHttp() {
        Fixture fixture = createFixture();
        BorrowingDTO first = borrow(fixture);
        ResponseEntity<ErrorResponseDTO> rejected = http.postForEntity("/borrowings",
                Map.of("bookId", fixture.bookId(), "memberId", fixture.memberId()), ErrorResponseDTO.class);

        assertEquals(HttpStatus.CONFLICT, rejected.getStatusCode());
        assertNotNull(rejected.getBody());
        assertEquals("Book is unavailable", rejected.getBody().error());
        assertNotNull(rejected.getBody().timestamp());
        ResponseEntity<BorrowingDTO[]> active = http.getForEntity("/members/{id}/borrowings",
                BorrowingDTO[].class, fixture.memberId());
        assertEquals(HttpStatus.OK, active.getStatusCode());
        assertNotNull(active.getBody());
        assertEquals(1, active.getBody().length);
        assertEquals(first.id(), active.getBody()[0].id());
    }

    private Fixture createFixture() {
        String token = UUID.randomUUID().toString();
        ResponseEntity<BookDTO> book = http.postForEntity("/books",
                new BookDTO(null, token, "HTTP fixture " + token, "Author", "Testing", 2026, true), BookDTO.class);
        assertEquals(HttpStatus.CREATED, book.getStatusCode());
        assertNotNull(book.getBody());
        assertNotNull(book.getBody().id());
        ResponseEntity<MemberDTO> member = http.postForEntity("/members",
                new MemberDTO(null, "HTTP member " + token, token + "@example.com", LocalDate.now(), true),
                MemberDTO.class);
        assertEquals(HttpStatus.CREATED, member.getStatusCode());
        assertNotNull(member.getBody());
        assertNotNull(member.getBody().id());
        return new Fixture(book.getBody().id(), member.getBody().id());
    }

    private BorrowingDTO borrow(Fixture fixture) {
        ResponseEntity<BorrowingDTO> response = http.postForEntity("/borrowings",
                Map.of("bookId", fixture.bookId(), "memberId", fixture.memberId()), BorrowingDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        return response.getBody();
    }

    private record Fixture(Long bookId, Long memberId) { }
}
