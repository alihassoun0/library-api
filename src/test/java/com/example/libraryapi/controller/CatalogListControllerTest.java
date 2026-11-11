package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogListControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private BookRepository books;
    @Autowired private MemberRepository members;

    @Test
    void listBooks_shouldContainPersistedBookWithItsActualFields() throws Exception {
        Book book = new Book();
        book.setTitle("Catalog regression");
        book.setIsbn("catalog-test-isbn");
        book.setAvailable(false);
        book = books.saveAndFlush(book);

        String body = mvc.perform(get("/books")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode match = findById(json.readTree(body), book.getId());

        assertEquals("Catalog regression", match.get("title").asText());
        assertEquals("catalog-test-isbn", match.get("isbn").asText());
        assertFalse(match.get("available").asBoolean());
    }

    @Test
    void listMembers_shouldContainPersistedMemberWithItsActualFields() throws Exception {
        Member member = new Member();
        member.setName("Catalog member");
        member.setEmail("catalog@example.com");
        member.setActive(false);
        member = members.saveAndFlush(member);

        String body = mvc.perform(get("/members")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode match = findById(json.readTree(body), member.getId());

        assertEquals("Catalog member", match.get("name").asText());
        assertEquals("catalog@example.com", match.get("email").asText());
        assertFalse(match.get("active").asBoolean());
    }

    private JsonNode findById(JsonNode response, long id) {
        assertTrue(response.isArray());
        return StreamSupport.stream(response.spliterator(), false)
                .filter(row -> row.get("id").asLong() == id).findFirst()
                .orElseThrow(() -> new AssertionError("Saved record missing from list: " + id));
    }
}
