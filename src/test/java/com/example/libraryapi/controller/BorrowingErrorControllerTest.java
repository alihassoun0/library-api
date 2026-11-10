package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.service.BorrowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BorrowingErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingService borrowingService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setGenre("Programming");
        book.setPublishedYear(2008);
        book.setAvailable(true);
        book = bookRepository.save(book);

        member = new Member();
        member.setName("Error Test Member");
        member.setEmail("errors@example.com");
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        member = memberRepository.save(member);
    }

    @Test
    void borrowBook_shouldReturnNotFoundForMissingBook() throws Exception {
        mockMvc.perform(post("/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":-1,\"memberId\":" + member.getId() + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_shouldReturnNotFoundForMissingMember() throws Exception {
        mockMvc.perform(post("/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + book.getId() + ",\"memberId\":-1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnBook_shouldReturnNotFoundForMissingBorrowing() throws Exception {
        mockMvc.perform(put("/borrowings/{id}/return", -1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActiveBorrowings_shouldReturnNotFoundForMissingMember() throws Exception {
        mockMvc.perform(get("/members/{id}/borrowings", -1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_shouldReturnConflictForUnavailableBook() throws Exception {
        borrowingService.borrowBook(book.getId(), member.getId());

        mockMvc.perform(post("/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + book.getId()
                                + ",\"memberId\":" + member.getId() + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book is unavailable"));
    }

    @Test
    void returnBook_shouldReturnConflictForRepeatedReturn() throws Exception {
        Borrowing borrowing = borrowingService.borrowBook(book.getId(), member.getId());
        borrowingService.returnBook(borrowing.getId());

        mockMvc.perform(put("/borrowings/{id}/return", borrowing.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book has already been returned"));
    }
}
