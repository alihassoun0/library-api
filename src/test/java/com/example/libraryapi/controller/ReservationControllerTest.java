package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.entity.Reservation;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.repository.ReservationRepository;
import com.example.libraryapi.service.ReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = saveBook("HTTP Reservation Book");
        member = saveMember("HTTP Reservation Member");
    }

    @Test
    void ac1_postAvailableBook_shouldReturn409AndExactMessage() throws Exception {
        book.setAvailable(true);
        bookRepository.save(book);
        long countBefore = reservationRepository.count();

        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), member.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book is available for direct borrowing."));

        assertEquals(countBefore, reservationRepository.count());
    }

    @Test
    void ac2_postReservation_shouldReturn201AndPersistExpectedFields() throws Exception {
        String response = mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), member.getId())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.book.id").value(book.getId().intValue()))
                .andExpect(jsonPath("$.member.id").value(member.getId().intValue()))
                .andExpect(jsonPath("$.reservationDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();
        JsonNode result = objectMapper.readTree(response);
        assertEquals(5, result.size());
        assertTrue(result.get("id").isIntegralNumber());
        entityManager.flush();
        entityManager.clear();

        Reservation saved = reservationRepository.findById(result.get("id").longValue()).orElseThrow();
        assertEquals(book.getId(), saved.getBook().getId());
        assertEquals(member.getId(), saved.getMember().getId());
        assertEquals(LocalDate.now(), saved.getReservationDate());
        assertTrue(saved.isActive());
        assertFalse(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    void ac3_getReservations_shouldReturnOnlyMembersActiveRecords() throws Exception {
        Reservation first = reservationService.reserveBook(book.getId(), member.getId());
        Book secondBook = saveBook("Second HTTP Book");
        Reservation second = reservationService.reserveBook(secondBook.getId(), member.getId());
        Book thirdBook = saveBook("Cancelled HTTP Book");
        Reservation cancelled = reservationService.reserveBook(thirdBook.getId(), member.getId());
        reservationService.cancelReservation(member.getId(), cancelled.getId());
        Member otherMember = saveMember("Other HTTP Member");
        reservationService.reserveBook(book.getId(), otherMember.getId());
        entityManager.flush();
        entityManager.clear();

        String response = mockMvc.perform(get("/members/{memberId}/reservations", member.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode results = objectMapper.readTree(response);
        assertTrue(results.isArray());
        assertEquals(2, results.size());
        Set<Long> ids = new HashSet<>();
        for (JsonNode result : results) {
            ids.add(result.get("id").longValue());
            assertTrue(result.get("active").booleanValue());
            assertEquals(member.getId().longValue(), result.get("member").get("id").longValue());
        }
        assertEquals(Set.of(first.getId(), second.getId()), ids);
    }

    @Test
    void ac4_deleteReservation_shouldReturn204RetainInactiveRecordAndExcludeFromList() throws Exception {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());

        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}",
                        member.getId(), reservation.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        entityManager.flush();
        entityManager.clear();

        Reservation saved = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertFalse(saved.isActive());
        assertFalse(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
        mockMvc.perform(get("/members/{memberId}/reservations", member.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void postReservation_shouldReturn404ForMissingBook() throws Exception {
        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(-1L, member.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"));
    }

    @Test
    void postReservation_shouldReturn404ForMissingMember() throws Exception {
        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), -1L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Member not found"));
    }

    @Test
    void postReservation_shouldReturn409ForInactiveMember() throws Exception {
        member.setActive(false);
        memberRepository.save(member);

        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), member.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    void postReservation_shouldReturn409ForActiveDuplicateWithoutSavingAnother() throws Exception {
        reservationService.reserveBook(book.getId(), member.getId());
        long countBefore = reservationRepository.count();

        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), member.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isString());

        assertEquals(countBefore, reservationRepository.count());
    }

    @Test
    void postReservation_shouldAllowRecreationAfterCancellation() throws Exception {
        Reservation original = reservationService.reserveBook(book.getId(), member.getId());
        reservationService.cancelReservation(member.getId(), original.getId());

        String response = mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(book.getId(), member.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();

        assertNotEquals(original.getId().longValue(), objectMapper.readTree(response).get("id").longValue());
        assertFalse(reservationRepository.findById(original.getId()).orElseThrow().isActive());
    }

    @Test
    void postReservation_shouldKeepIdDateAndActiveStateServerControlled() throws Exception {
        String body = "{\"bookId\":" + book.getId() + ",\"memberId\":" + member.getId()
                + ",\"id\":-123,\"reservationDate\":\"2000-01-01\",\"active\":false}";

        String response = mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();

        assertNotEquals(-123L, objectMapper.readTree(response).get("id").longValue());
    }

    @Test
    void getReservations_shouldReturn200AndEmptyArrayForExistingMember() throws Exception {
        mockMvc.perform(get("/members/{memberId}/reservations", member.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getReservations_shouldReturn404ForMissingMember() throws Exception {
        mockMvc.perform(get("/members/{memberId}/reservations", -1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Member not found"));
    }

    @Test
    void deleteReservation_shouldReturn404ForMissingMember() throws Exception {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());

        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}", -1L, reservation.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Member not found"));

        assertTrue(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
    }

    @Test
    void deleteReservation_shouldReturn404ForMissingReservation() throws Exception {
        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}", member.getId(), -1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Reservation not found"));
    }

    @Test
    void deleteReservation_shouldReturn404ForAnotherMembersRecordAndPreserveIt() throws Exception {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        Member otherMember = saveMember("Other HTTP Member");

        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}",
                        otherMember.getId(), reservation.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Reservation not found"));

        entityManager.flush();
        entityManager.clear();
        assertTrue(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
    }

    @Test
    void deleteReservation_shouldReturn409ForRepeatedCancellation() throws Exception {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        reservationService.cancelReservation(member.getId(), reservation.getId());

        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}",
                        member.getId(), reservation.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    void deleteReservation_shouldAllowInactiveMemberAndNowAvailableBook() throws Exception {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        member.setActive(false);
        book.setAvailable(true);
        memberRepository.save(member);
        bookRepository.save(book);

        mockMvc.perform(delete("/members/{memberId}/reservations/{reservationId}",
                        member.getId(), reservation.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        entityManager.flush();
        entityManager.clear();

        assertFalse(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
        assertFalse(memberRepository.findById(member.getId()).orElseThrow().isActive());
        assertTrue(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"bookId\":null,\"memberId\":null}",
            "{\"memberId\":1}", "{\"bookId\":1}",
            "{\"bookId\":\"abc\",\"memberId\":1}", "{", "null", "[]", ""})
    void postReservation_shouldReturn400ForInvalidOrMissingIds(String body) throws Exception {
        long countBefore = reservationRepository.count();

        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());

        assertEquals(countBefore, reservationRepository.count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/members/invalid/reservations",
            "/members/invalid/reservations/1", "/members/1/reservations/invalid"})
    void memberEndpoints_shouldReturn400ForMalformedPathIds(String path) throws Exception {
        if (path.endsWith("/reservations")) {
            mockMvc.perform(get(path)).andExpect(status().isBadRequest());
        } else {
            mockMvc.perform(delete(path)).andExpect(status().isBadRequest());
        }
    }

    private String requestBody(Long bookId, Long memberId) {
        return "{\"bookId\":" + bookId + ",\"memberId\":" + memberId + "}";
    }

    private Book saveBook(String title) {
        Book savedBook = new Book();
        savedBook.setIsbn("test-" + title);
        savedBook.setTitle(title);
        savedBook.setAuthor("Test Author");
        savedBook.setGenre("Engineering");
        savedBook.setPublishedYear(2020);
        savedBook.setAvailable(false);
        return bookRepository.save(savedBook);
    }

    private Member saveMember(String name) {
        Member savedMember = new Member();
        savedMember.setName(name);
        savedMember.setEmail(name.replace(" ", ".") + "@example.com");
        savedMember.setMembershipDate(LocalDate.now());
        savedMember.setActive(true);
        return memberRepository.save(savedMember);
    }
}
