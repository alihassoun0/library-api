# Lab 2 — Step D: XP pair review

Status: review recorded before the improvements below were implemented. This is the AI review; the student's personal response belongs in `reports/lab2-report.md`.

## Review prompt

Review the BorrowingService and BorrowingController you just wrote. Act as a senior code reviewer in an XP team. List any concerns about:
- Error handling and edge cases
- Test coverage gaps
- Code readability
- Any XP principle you think was violated

## Review output

### R1 — IMPORTANT: invalid operations need an HTTP response

`BorrowingService.borrowBook()` and `returnBook()` throw `IllegalStateException` when a book is unavailable or already returned. The controller currently has no handler for it. An HTTP caller consequently receives a server error rather than a domain conflict; MockMvc exposes the unhandled exception. Keep the service's existing exception contract, add a controller-local HTTP 409 mapping, and write tests for both operations first. Also verify missing books, members, and borrowings remain HTTP 404. A global, consistent error-response design is assigned in Lab 4 and should stay there.

### R2 — IMPORTANT: fine tests are integration tests rather than direct unit tests

`BorrowingFineTest.returnBook_shouldCalculateFine()` verifies five useful outcomes through `returnBook()`, but never invokes `calculateFine()` directly and starts a Spring context. Step C explicitly asks for unit tests of the pure method. Add fixed-date, no-Spring tests for on-time, exactly due, one day late, seven days late, and before due. Use decimal strings/BigDecimal; confirm zero results satisfy the documented BigDecimal.ZERO contract. Keep the existing tests.

### R3 — IMPORTANT: match the specified controller placement

The assignment places `GET /members/{id}/borrowings` in `BorrowingController`; the current implementation puts it in `MemberController`. Move the mapping into the specified class, keeping the public URL and response unchanged. Use separate method-level `/members/...` and `/borrowings/...` paths to avoid accidentally exposing `/borrowings/members/...`. The existing MockMvc test is a regression oracle.

### R4 — IMPORTANT: re-reading managed entities is limited persistence evidence

Within a transactional test, repository `findById()` may return the same managed entity. The current return endpoint test flushes but does not clear the persistence context. Its assertions remain useful, but do not independently prove a database reload. Preserve that test and add a separate flush-and-clear regression test before asserting saved return date, availability, and fine.

### R5 — MINOR: explain the policy literals

`14` and `new BigDecimal("0.50")` are correct but unnamed policy values. Named constants would improve readability and isolate policy changes. Defer this change to Lab 5, which explicitly assigns Replace Magic Number; do not silently count the future refactoring now.

### R6 — IMPORTANT for concurrent use: transactions alone do not serialize borrowing

Two concurrent transactions can both observe an available book. A locking/versioning strategy and a concurrency test would be needed for production protection. Defer that extension for this small simulated sprint; do not claim the current transaction annotation provides concurrency guarantees.

### R7 — MINOR: clock boundary and undefined inputs

Calls to `LocalDate.now()` around midnight can differ across a test and the service. Direct fixed-date fine tests avoid that issue. A Clock dependency would make date-sensitive integration tests deterministic but is a larger API/setup change than this sprint requires. Null fine dates, missing request IDs, and reversed borrow/due dates also lack a specified policy; do not invent one. Record these limitations rather than implementing extra business rules.

### XP process assessment

The original US-01 and US-02/03/04 tests were prepared before their implementations in the conversation. They were not committed as RED states then; the initial checkpoint `e629c25` contains both code and tests. Later commit `3fedd09` genuinely records the missing return-endpoint test before its implementation: 9 cases, one HTTP 404-vs-200 failure, and eight passing existing cases. Preserve that history; do not backdate commits or describe the checkpoint as original test-first evidence. Keep feedback small, run RED then GREEN, retain all existing assertions, and let the student review decisions and supply personal reflections.

## Proposed decisions

- Apply R1 and R2 as the two primary review improvements.
- Apply R3 for exact assignment alignment and R4 for stronger persistence evidence.
- Defer R5 to its explicitly assigned Lab 5 step.
- Defer R6 and the broader changes in R7; record their practical limits.
- Student review/personal agreement: **PENDING HUMAN INPUT**. These decisions describe the AI's implementation rationale and are not a fabricated student reflection.

Execution results and commits are recorded in `docs/lab2-evidence.md`.
