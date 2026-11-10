package com.example.libraryapi.service;

import com.example.libraryapi.entity.Book;
import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.entity.Reservation;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.BorrowingRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.repository.ReservationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = saveBook("Reservation Book");
        member = saveMember("Reservation Member");
    }

    @Test
    void ac1_availableBook_shouldBeRejectedWithExactMessage() {
        book.setAvailable(true);
        bookRepository.save(book);
        long countBefore = reservationRepository.count();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reservationService.reserveBook(book.getId(), member.getId()));

        assertEquals("Book is available for direct borrowing.", exception.getMessage());
        assertEquals(countBefore, reservationRepository.count());
        assertTrue(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    void ac2_reserveBook_shouldPersistGeneratedIdRelationshipsDateAndActiveState() {
        Reservation result = reservationService.reserveBook(book.getId(), member.getId());
        assertNotNull(result.getId());
        entityManager.flush();
        entityManager.clear();

        Reservation saved = reservationRepository.findById(result.getId()).orElseThrow();
        assertAll(
                () -> assertEquals(book.getId(), saved.getBook().getId()),
                () -> assertEquals(member.getId(), saved.getMember().getId()),
                () -> assertEquals(LocalDate.now(), saved.getReservationDate()),
                () -> assertTrue(saved.isActive()),
                () -> assertFalse(bookRepository.findById(book.getId()).orElseThrow().isAvailable())
        );
    }

    @Test
    void ac3_getActiveReservations_shouldExcludeCancelledAndOtherMembersRecords() {
        Reservation first = saveReservation(book, member, true);
        Reservation second = saveReservation(saveBook("Second Book"), member, true);
        saveReservation(saveBook("Cancelled Book"), member, false);
        saveReservation(book, saveMember("Other Member"), true);
        entityManager.flush();
        entityManager.clear();

        List<Reservation> results = reservationService.getActiveReservations(member.getId());

        assertEquals(2, results.size());
        assertEquals(Set.of(first.getId(), second.getId()), results.stream()
                .map(Reservation::getId).collect(Collectors.toSet()));
        assertTrue(results.stream().allMatch(Reservation::isActive));
        assertTrue(results.stream().allMatch(r -> r.getMember().getId().equals(member.getId())));
    }

    @Test
    void ac4_cancelReservation_shouldPersistInactiveRecordAndExcludeItFromListing() {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());

        reservationService.cancelReservation(member.getId(), reservation.getId());
        entityManager.flush();
        entityManager.clear();

        Reservation saved = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertFalse(saved.isActive());
        assertEquals(member.getId(), saved.getMember().getId());
        assertTrue(reservationService.getActiveReservations(member.getId()).isEmpty());
        assertFalse(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    void reserveBook_shouldRejectMissingBook() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.reserveBook(-1L, member.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void reserveBook_shouldRejectMissingMember() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.reserveBook(book.getId(), -1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void reserveBook_shouldRejectInactiveMemberWithoutSaving() {
        member.setActive(false);
        memberRepository.save(member);
        long countBefore = reservationRepository.count();

        assertThrows(IllegalStateException.class,
                () -> reservationService.reserveBook(book.getId(), member.getId()));

        assertEquals(countBefore, reservationRepository.count());
    }

    @Test
    void reserveBook_shouldRejectDuplicateActiveReservation() {
        Reservation original = reservationService.reserveBook(book.getId(), member.getId());
        long countBefore = reservationRepository.count();

        assertThrows(IllegalStateException.class,
                () -> reservationService.reserveBook(book.getId(), member.getId()));

        assertEquals(countBefore, reservationRepository.count());
        assertTrue(reservationRepository.findById(original.getId()).orElseThrow().isActive());
    }

    @Test
    void reserveBook_shouldAllowNewReservationAfterCancellation() {
        Reservation original = reservationService.reserveBook(book.getId(), member.getId());
        reservationService.cancelReservation(member.getId(), original.getId());

        Reservation replacement = reservationService.reserveBook(book.getId(), member.getId());
        entityManager.flush();
        entityManager.clear();

        assertNotEquals(original.getId(), replacement.getId());
        assertFalse(reservationRepository.findById(original.getId()).orElseThrow().isActive());
        assertTrue(reservationRepository.findById(replacement.getId()).orElseThrow().isActive());
    }

    @Test
    void reserveBook_shouldAllowDifferentMembersToReserveSameBook() {
        Reservation first = reservationService.reserveBook(book.getId(), member.getId());
        Member otherMember = saveMember("Other Member");

        Reservation second = reservationService.reserveBook(book.getId(), otherMember.getId());

        assertNotEquals(first.getId(), second.getId());
        assertEquals(otherMember.getId(), second.getMember().getId());
    }

    @Test
    void getActiveReservations_shouldReturnEmptyListForExistingMember() {
        assertTrue(reservationService.getActiveReservations(member.getId()).isEmpty());
    }

    @Test
    void getActiveReservations_shouldRejectMissingMember() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.getActiveReservations(-1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void cancelReservation_shouldRejectMissingMember() {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.cancelReservation(-1L, reservation.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
    }

    @Test
    void cancelReservation_shouldRejectMissingReservation() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.cancelReservation(member.getId(), -1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void cancelReservation_shouldRejectAnotherMembersReservationWithoutChangingIt() {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        Member otherMember = saveMember("Other Member");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reservationService.cancelReservation(otherMember.getId(), reservation.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
    }

    @Test
    void cancelReservation_shouldRejectRepeatedCancellation() {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        reservationService.cancelReservation(member.getId(), reservation.getId());

        assertThrows(IllegalStateException.class,
                () -> reservationService.cancelReservation(member.getId(), reservation.getId()));
        assertFalse(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
    }

    @Test
    void cancelReservation_shouldAllowInactiveMemberAndNowAvailableBook() {
        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        member.setActive(false);
        book.setAvailable(true);
        memberRepository.save(member);
        bookRepository.save(book);

        reservationService.cancelReservation(member.getId(), reservation.getId());
        entityManager.flush();
        entityManager.clear();

        assertFalse(reservationRepository.findById(reservation.getId()).orElseThrow().isActive());
        assertFalse(memberRepository.findById(member.getId()).orElseThrow().isActive());
        assertTrue(bookRepository.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    void reservationLifecycle_shouldNotModifyBookMemberOrBorrowingData() {
        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now().minusDays(7));
        borrowing.setDueDate(LocalDate.now().plusDays(7));
        borrowing.setFineAmount(BigDecimal.ZERO);
        borrowing = borrowingRepository.save(borrowing);
        entityManager.flush();
        JsonNode originalBook = objectMapper.valueToTree(book);
        JsonNode originalMember = objectMapper.valueToTree(member);
        JsonNode originalBorrowing = objectMapper.valueToTree(borrowing);
        long borrowingCount = borrowingRepository.count();

        Reservation reservation = reservationService.reserveBook(book.getId(), member.getId());
        reservationService.getActiveReservations(member.getId());
        reservationService.cancelReservation(member.getId(), reservation.getId());
        entityManager.flush();
        entityManager.clear();

        assertEquals(originalBook, objectMapper.valueToTree(bookRepository.findById(book.getId()).orElseThrow()));
        assertEquals(originalMember, objectMapper.valueToTree(memberRepository.findById(member.getId()).orElseThrow()));
        assertEquals(originalBorrowing,
                objectMapper.valueToTree(borrowingRepository.findById(borrowing.getId()).orElseThrow()));
        assertEquals(borrowingCount, borrowingRepository.count());
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

    private Reservation saveReservation(Book reservedBook, Member reservingMember, boolean active) {
        Reservation reservation = new Reservation();
        reservation.setBook(reservedBook);
        reservation.setMember(reservingMember);
        reservation.setReservationDate(LocalDate.now());
        reservation.setActive(active);
        return reservationRepository.save(reservation);
    }
}
