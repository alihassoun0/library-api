package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.service.BorrowingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BorrowingService borrowingService;

    @Test
    void returnBook_shouldReturnUpdatedBorrowingAndRestoreAvailability() throws Exception {
        Book book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setGenre("Programming");
        book.setPublishedYear(2008);
        book.setAvailable(true);
        book = bookRepository.save(book);

        Member member = new Member();
        member.setName("Return Endpoint Test Member");
        member.setEmail("return.endpoint@example.com");
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        member = memberRepository.save(member);

        Borrowing borrowing = borrowingService.borrowBook(book.getId(), member.getId());

        String response = mockMvc.perform(put("/borrowings/{id}/return", borrowing.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode returnedBorrowing = objectMapper.readTree(response);
        assertEquals(borrowing.getId().longValue(), returnedBorrowing.get("id").longValue());
        assertEquals(LocalDate.now().toString(), returnedBorrowing.get("returnDate").asText());
        JsonNode fineAmount = returnedBorrowing.get("fineAmount");
        assertNotNull(fineAmount);
        assertTrue(fineAmount.isNumber());
        assertEquals(0, BigDecimal.ZERO.compareTo(fineAmount.decimalValue()));

        borrowingRepository.flush();
        bookRepository.flush();
        Borrowing savedBorrowing = borrowingRepository.findById(borrowing.getId()).orElseThrow();
        assertEquals(LocalDate.now(), savedBorrowing.getReturnDate());
        assertNotNull(savedBorrowing.getFineAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(savedBorrowing.getFineAmount()));

        Book returnedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertTrue(returnedBook.isAvailable());
    }
}
