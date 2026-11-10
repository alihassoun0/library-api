package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberBorrowingControllerTest {

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

    @Test
    void getMemberBorrowings_shouldReturnOnlyActiveBorrowings() throws Exception {
        Member member = saveMember("Target Member", "target@example.com");
        Member otherMember = saveMember("Other Member", "other@example.com");

        Borrowing firstActive = saveBorrowing(member, "First active book", null);
        Borrowing secondActive = saveBorrowing(member, "Second active book", null);
        saveBorrowing(member, "Returned book", LocalDate.now().minusDays(1));
        saveBorrowing(otherMember, "Other member's book", null);
        borrowingRepository.flush();

        String response = mockMvc.perform(get("/members/{id}/borrowings", member.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode results = objectMapper.readTree(response);
        assertTrue(results.isArray());
        assertEquals(2, results.size());

        Set<Long> actualIds = new HashSet<>();
        for (JsonNode result : results) {
            actualIds.add(result.get("id").longValue());
            assertEquals(member.getId().longValue(), result.get("member").get("id").longValue());
            assertTrue(result.has("returnDate"));
            assertTrue(result.get("returnDate").isNull());
        }
        assertEquals(Set.of(firstActive.getId(), secondActive.getId()), actualIds);
    }

    private Member saveMember(String name, String email) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        return memberRepository.save(member);
    }

    private Borrowing saveBorrowing(Member member, String title, LocalDate returnDate) {
        Book book = new Book();
        book.setIsbn("test-" + title);
        book.setTitle(title);
        book.setAuthor("Test Author");
        book.setGenre("Test");
        book.setPublishedYear(2020);
        book.setAvailable(returnDate != null);
        book = bookRepository.save(book);

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now().minusDays(7));
        borrowing.setDueDate(LocalDate.now().plusDays(7));
        borrowing.setReturnDate(returnDate);
        return borrowingRepository.save(borrowing);
    }
}
