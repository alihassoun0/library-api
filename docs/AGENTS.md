# AGENTS.md — Library API: Book Reservation Feature

## Project context

This is a Spring Boot 3 REST API for a university library.
Existing entities: Book, Member, Borrowing.
See the existing src/ directory for current code and conventions.

Use the existing Java 17 project and `com.example.libraryapi` package structure.
The authoritative assignment is `SE_Lab_Assignment_2026_2027_Codex.docx`, Lab 3.
The student approved the eight business rules and fields below, and selected
the three endpoint paths. Production implementation requires Step B approval.

The professor supplies AC-1 verbatim and leaves AC-2 through AC-4 as placeholders.
The remaining three criteria below complete those placeholders using the
approved creation, listing, and cancellation behavior; there are exactly four.

## Feature: Book Reservation

### Purpose

A member may want a book that is currently borrowed by someone else.
A reservation records that interest and lets the member view or cancel it.
This feature records interest only; it does not automatically allocate books.

### Business rules

- Rule 1: Creating a reservation requires an existing Book and Member.
  Return HTTP 404 if either record is missing. Listing requires an existing
  Member; cancellation requires an existing Member and its Reservation.
- Rule 2: A member can only reserve a book that is currently unavailable
  (`available = false`). Otherwise return HTTP 409 with the exact message
  "Book is available for direct borrowing."
- Rule 3: Only active members (`active = true`) may create reservations.
  Reject creation by an inactive member with HTTP 409.
- Rule 4: A member cannot have more than one active reservation for the same
  book. Reject duplicate active reservations with HTTP 409. A cancelled record
  does not prevent a new reservation when the creation rules are satisfied.
- Rule 5: A successful creation saves a Reservation with a generated ID,
  the resolved Book and Member, `reservationDate = LocalDate.now()`, and
  `active = true`. The server controls the generated ID, date, and active flag.
- Rule 6: A member's reservation list contains only records for that member
  where `active = true`. Return HTTP 200 and an empty JSON array when an
  existing member has no active reservations. Response order is unspecified.
- Rule 7: Cancellation changes the member's Reservation to `active = false`
  without deleting its database record. Reject a repeated cancellation with
  HTTP 409. Cancellation does not require the member to remain active or the
  book to remain unavailable; those checks apply only to creation.
- Rule 8: Creating, listing, or cancelling reservations must not change any
  Book availability, Member data, or Borrowing record.

### New entity: Reservation

| Field | Java type | Persistence/meaning |
|---|---|---|
| id | Long | Generated identity primary key |
| book | Book | Unidirectional ManyToOne, required, book_id |
| member | Member | Unidirectional ManyToOne, required, member_id |
| reservationDate | LocalDate | Required server-assigned creation date |
| active | boolean | True on creation; false after cancellation |

Map the entity to `reservations`. Use a no-argument constructor and getters
and setters, consistent with the existing entities. Do not add inverse
reservation collections to Book or Member.

### REST endpoints required

| Method | Path | Request body | Response | HTTP status codes |
|---|---|---|---|---|
| POST | /reservations | JSON: `{"bookId": 1, "memberId": 1}` | Saved Reservation JSON with the five specified fields; book and member are nested existing entity representations | 201 created; 400 invalid request; 404 missing book/member; 409 unavailable-policy, inactive-member, or duplicate conflict |
| GET | /members/{memberId}/reservations | None | JSON array of that member's active Reservations | 200; 400 malformed path ID; 404 missing member |
| DELETE | /members/{memberId}/reservations/{reservationId} | None | No body; persist cancellation | 204 cancelled; 400 malformed path ID; 404 missing member or member-scoped reservation; 409 already cancelled |

For Rule 2, HTTP 409 contains
`{"error":"Book is available for direct borrowing."}`. Other expected 404/409
responses also use `{"error":"<descriptive message>"}`. Handle reservation
errors locally so existing controllers' responses are unaffected.

Proposed contract details, explicitly awaiting Step B approval:

- Decision A1: DELETE returns HTTP 204 and performs the soft cancellation in
  approved Rule 7, rather than physically removing the record.
- Decision A2: A reservation belonging to a different member is absent from
  the member-scoped DELETE resource and returns HTTP 404 without changing it.
- Decision A3: POST requires both IDs. Missing/null IDs, malformed JSON, and
  nonnumeric path IDs return HTTP 400. Well-formed IDs without matching
  records return HTTP 404. Use existing validation support; no dependencies.
- Decision A4: There is no new authentication, queue ordering, expiration,
  notification, automatic fulfillment, or borrowing integration in this lab.
  Member scoping checks the resource relationship; it is not authentication.

### Acceptance criteria (these will become test cases)

- AC-1: Given a book is available, when a member tries to reserve it,
  then the API returns HTTP 409 with message "Book is available for direct borrowing."
- AC-2: Given an existing unavailable book and an existing active member with
  no active reservation for that book, when POST /reservations supplies their
  IDs, then HTTP 201 returns the saved Reservation with a generated ID, the
  correct Book and Member, today's reservationDate, and active = true;
  book availability remains false.
- AC-3: Given a member with active and cancelled reservations and another
  member with active reservations, when GET /members/{memberId}/reservations
  is called, then HTTP 200 returns only the requested member's active records.
- AC-4: Given a member's active reservation, when
  DELETE /members/{memberId}/reservations/{reservationId} is called, then
  HTTP 204 has no body, the saved record remains with active = false, a
  subsequent member listing excludes it, and book availability is unchanged.

Test all eight rules and declared HTTP responses in addition to these four
criteria, including missing records, inactive members, duplicates, empty
lists, cross-member cancellation, repeated cancellation, and invalid requests.
Use `ReservationServiceTest.java` and `ReservationControllerTest.java` in the
existing test package structure, following the BorrowingServiceTest style.

### What Codex must NOT do

- Do not modify any existing entity or controller.
- Do not use any library not already in pom.xml.
- Do not add Flyway or Liquibase migrations.
- Do not change existing business logic or test assertions, add dependencies,
  create another project, or implement Lab 4 features.

### Code style conventions (observed from existing code)

- Keep entities, repositories, services, and controllers under the existing
  `com.example.libraryapi.entity`, `.repository`, `.service`, and `.controller`
  packages; use PascalCase classes and camelCase fields/methods.
- Follow Book/Member/Borrowing: JPA annotations, generated Long identity IDs,
  no-argument constructors, getters/setters, and unidirectional ManyToOne
  references. Use LocalDate for dates and JpaRepository<Entity, Long>.
- Follow BorrowingService: constructor injection, @Service, @Transactional
  for writes, @Transactional(readOnly = true) for reads, and repository
  lookups before enforcing business rules.
- Follow BorrowingController: thin REST methods, a request record containing
  IDs, ResponseEntity for explicit statuses, and controller-local error
  handling with an `error` JSON field. Return existing-style entity JSON.
- Follow BorrowingServiceTest and current controller tests: JUnit 5,
  @SpringBootTest, transactional database isolation, MockMvc for HTTP tests,
  and assertions against required behavior rather than generated values alone.
