# Lab 2 — Agile Methodologies with AI

**Status: technical work verified; submission INCOMPLETE pending student input and final Git submission.**

Authoritative source: `SE_Lab_Assignment_2026_2027_Codex.docx`, Lab 2, Steps A–E and its 10-point rubric. Project: the existing Library API, Java 17 / Spring Boot 3. No second project was created.

Objective: run one simulated Agile sprint with Codex and examine planning, velocity, TDD, and XP pair programming. The four learning objectives concern stories/tasks, story-driven implementation, test-first development, and personal estimated-versus-actual effort.

## 1. Velocity table

| ID | User story | Estimate (h) | Actual (h) | Status |
|---|---|---:|---:|---|
| US-01 | As a librarian, I can record a borrowing so that the system tracks which member has which book. | 3 | TBD — student input | DONE |
| US-02 | As a librarian, I can record a book return so that the book becomes available again. | 3 | TBD — student input | DONE |
| US-03 | As a member, I can see my active borrowings so that I know which books I currently have. | 2 | TBD — student input | DONE |
| US-04 | As a librarian, I can calculate the fine for an overdue book so that I can charge the right amount. (Fine = EUR 0.50 per overdue day.) | 2 | TBD — student input | DONE |
| **Total** | | **10** | **TBD** | **Stories implemented/tested** |

This table mirrors `docs/sprint-backlog.md`. Original estimates were present before implementation and have not been revised to fit the result. The assignment's suggested 2.5-hour lab duration is not the team's original 10-hour story estimate.

Velocity ratio = total actual story hours / total estimated story hours = **(A01 + A02 + A03 + A04) / 10**.

**PENDING HUMAN INPUT:** supply actual hours per story, identify any honest estimates reconstructed from memory, and explain what effort is included. Then calculate the numeric ratio and state whether it is above, below, or equal to 1.0. Neither elapsed terminal duration nor AI runtime is being used as personal effort. Numerical velocity remains unknown.

Engineering task breakdown and successful verification for each story are recorded in the backlog. DONE refers to story behavior; it does not mean the report, effort measurement, or final submission is complete.

## 2. TDD observation

The original `borrowBook_shouldMarkBookAsUnavailable()` was prepared before BorrowingService existed. The first Maven attempt hit a sandbox `.m2` permission problem; an approved retry resolved dependencies and reached the intended compilation error, `cannot find symbol: class BorrowingService`. After implementing US-01, the test passed and checked the generated borrowing ID, book unavailability, and due date 14 days ahead. The project's existing no-argument constructors/setters and `com.example.libraryapi` package were used to preserve the sample's behavior without introducing unavailable constructors or a second package tree.

US-02's separate return test and US-03/04's filtering/fine tests were also prepared before their production implementation in the conversation. Their first combined run could not compile because returnBook(Long) was absent. Once implemented, eight cases passed. This history is described as conversation evidence; those original RED states were not committed at the time.

Git subsequently recorded genuine test-first evidence for the missing return endpoint. Commit `3fedd09` introduced its test while the endpoint did not exist: **9 tests, 1 failure, 0 errors**, because HTTP 200 was expected but HTTP 404 was returned. Commit `b1a382a` added the endpoint and recorded **9 tests, 0 failures, 0 errors** in `reports/evidence/lab2/01-baseline.txt`. The earlier `e629c25` checkpoint contains implementation and tests together and is not misrepresented as a test-first commit.

The XP review produced a second genuine RED/GREEN cycle: `078fa77` adds HTTP conflict tests before the handler, with **21 tests, 0 failures, 2 errors** from unhandled IllegalStateException. After adding the local handler, the complete suite passed **21 tests, 0 failures, 0 errors, 0 skipped**. The raw logs are `reports/evidence/lab2/02-review-red.txt` and `03-review-green.txt`. Fixed-date fine unit tests were added to protect existing behavior; they were not falsely described as failing before a fine implementation that already existed.

**PENDING HUMAN INPUT:** how did writing the test first change your own understanding of the borrowing/return feature, expected outcomes, or implementation choices? Add your personal observation here; the factual sequence above is not a fabricated personal reflection.

## 3. XP pair reflection

The prescribed Step D review prompt and its complete recorded output follow. Codex acted as the driver and AI reviewer; the student remains responsible for reviewing and accepting the work.

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

## Implementation decisions and verification

- Applied R1: added six HTTP regression cases first; two exposed unhandled exceptions (21 tests, 0 failures, 2 errors). A controller-local IllegalStateException-to-409 handler makes all six pass while keeping missing-record responses at 404 and leaving service logic unchanged.
- Applied R2: added five fixed-date `BorrowingFineUnitTest` cases calling calculateFine directly without Spring, with numeric BigDecimal comparison and no repository interactions.
- Applied R3: moved the active-borrowings method into BorrowingController, preserving GET /members/{id}/borrowings and passing the unchanged existing HTTP test.
- Applied R4: added BorrowingPersistenceTest, flushing and clearing before checking database-reloaded availability, return date, and fine. Existing assertions were preserved.
- Defer R5 to its explicitly assigned Lab 5 step.
- Defer R6 and the broader changes in R7; record their practical limits.
- Student review/personal agreement: **PENDING HUMAN INPUT**. These decisions describe the AI's implementation rationale and are not a fabricated student reflection.

Execution results and commits are recorded in `docs/lab2-evidence.md`.

Final technical verification after these changes: `mvn test` — 21 tests, 0 failures, 0 errors, 0 skipped. This is not a claim that the human report and submission steps are complete.


### Applied suggestions and rationale

- **R1, error handling:** a controller-local handler returns HTTP 409 for unavailable-book and repeated-return conflicts, retaining the service's IllegalStateException contract. Six HTTP regression cases include four missing-record checks. This improves the REST contract without prematurely implementing Lab 4's global handler.
- **R2, unit-test gap:** five direct fine tests call the pure method with fixed dates and BigDecimal expected values, requiring no Spring context or database. Existing integration tests remain unchanged.
- **R3, specification alignment:** GET /members/{id}/borrowings now belongs to BorrowingController as assigned, with its original URL and regression assertions preserved.
- **R4, persistence evidence:** a new test flushes and clears the persistence context before checking database-reloaded book availability, return date, and fine.

### Deferred suggestions and rationale

- **R5, named policy constants:** deferred to Lab 5's explicitly assigned Replace Magic Number exercise, so its before/after refactoring can be demonstrated honestly.
- **R6, concurrency locking:** deferred beyond the current sprint's required behavior. Transactions provide atomic updates but do not prevent two simultaneous borrowers from both reading availability as true.
- **R7, Clock injection and undefined input policies:** deferred to avoid inventing business rules or widening the public service API. Direct fine tests already use fixed dates. Midnight rollover remains a limitation of the existing today-based integration tests.

**PENDING HUMAN INPUT:** confirm which review suggestions you personally accepted or rejected and why. The implementation decisions above are the AI's engineering rationale; they must not be presented as the student's personal decisions without their review.

## 4. AI and estimation

The ratio cannot yet establish whether actual effort was above or below the original estimate because actual hours are pending. A ratio below 1.0 means less actual effort than estimated; above 1.0 means more; equal to 1.0 means the estimate matched. This compares effort with an estimate and does not independently prove AI caused a productivity change.

Observed workflow facts include permission-related dependency/Git friction, early compilation feedback from test-first work, the missing HTTP endpoint caught by MockMvc, the two HTTP error mappings caught during review, and the benefit of inspecting the professor's original assignment instead of relying on an incomplete feature summary. These are specific observations available for the student's reflection, not invented personal experience or measured productivity claims.

**PENDING HUMAN INPUT:** after supplying hours and computing velocity, explain whether AI felt faster or slower on this first sprint, connect that experience to tool maturity/team experience, and predict sprint 2 with a reason.

## Verification and submission status

Latest full Maven suite: **21 tests, 0 failures, 0 errors, 0 skipped — BUILD SUCCESS**. All original assertions and BorrowingService business logic are preserved; `docs/lab2-evidence.md` records the comparison commands. No coverage percentage is claimed for Lab 2; JaCoCo belongs to the later assigned analysis/testing labs.

| Grading criterion | Points | Verification |
|---|---:|---|
| All four user stories implemented and passing tests | 4 | Verified against the complete suite; endpoint and fine-unit-test gaps addressed |
| At least one test before implementation, shown in Git | 3 | RED return endpoint commit 3fedd09 precedes GREEN b1a382a; review RED 078fa77 precedes its handler fix |
| Report answers all questions with specific observations | 3 | **Incomplete:** student hours, numeric velocity, review response, and personal reflections pending |

Final prescribed commit message: `lab2: agile sprint — borrowing feature, TDD, XP pair review`. Final tag `lab-2` and its push are **pending** until every report/rubric requirement is complete. Intermediate Lab 2 commits are progress checkpoints, not a completed submission.

Lab 1 history/tag are preserved. Lab 3 has not started. `docs/reference-labs-checklist.md` tracks the sequential gates and future required artifacts. Optional screenshot stages are listed in `docs/lab2-evidence.md`; Lab 2 itself does not require a screenshot, and no image capture is claimed.
