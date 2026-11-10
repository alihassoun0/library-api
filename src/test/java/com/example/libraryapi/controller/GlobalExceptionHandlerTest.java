package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.service.BookService;
import com.example.libraryapi.service.BorrowingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BorrowingService borrowingService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookService bookService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Global handler test");
        book.setAvailable(true);
        book = bookRepository.save(book);
        member = new Member();
        member.setName("Handler Member");
        member.setEmail("handler@example.com");
        member.setActive(true);
        member.setMembershipDate(LocalDate.now());
        member = memberRepository.save(member);
    }

    @Test
    void nonexistentBorrowingBook_shouldReturn404WithConsistentBody() throws Exception {
        String response = mockMvc.perform(post("/borrowings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":-1,\"memberId\":" + member.getId() + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"))
                .andExpect(jsonPath("$.timestamp").isString())
                .andReturn().getResponse().getContentAsString();
        assertTimestamp(response);
    }

    @Test
    void alreadyBorrowedBook_shouldReturn409WithConsistentBody() throws Exception {
        borrowingService.borrowBook(book.getId(), member.getId());
        String response = mockMvc.perform(post("/borrowings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + book.getId() + ",\"memberId\":" + member.getId() + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book is unavailable"))
                .andExpect(jsonPath("$.timestamp").isString())
                .andReturn().getResponse().getContentAsString();
        assertTimestamp(response);
    }

    @Test
    void missingRequiredIds_shouldReturn400WithFieldErrors() throws Exception {
        String response = mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.bookId").isString())
                .andExpect(jsonPath("$.fieldErrors.memberId").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andReturn().getResponse().getContentAsString();
        assertTimestamp(response);
    }

    @Test
    void malformedJson_shouldReturn400WithTimestamp() throws Exception {
        mockMvc.perform(post("/borrowings").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void entityNotFound_shouldReturn404() throws Exception {
        when(bookService.getBookById(-1L)).thenThrow(new EntityNotFoundException("Entity missing"));
        mockMvc.perform(get("/books/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entity missing"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void noSuchElement_shouldReturn404() throws Exception {
        when(bookService.getBookById(-1L)).thenThrow(new NoSuchElementException("Element missing"));
        mockMvc.perform(get("/books/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Element missing"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void unexpectedException_shouldReturn500WithoutInternalMessage() throws Exception {
        when(bookService.getBookById(-1L)).thenThrow(new UnsupportedOperationException("Internal sensitive detail"));
        mockMvc.perform(get("/books/-1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    private void assertTimestamp(String response) throws Exception {
        assertNotNull(Instant.parse(objectMapper.readTree(response).get("timestamp").asText()));
    }
}
