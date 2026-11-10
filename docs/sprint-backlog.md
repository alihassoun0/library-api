# Sprint 1 — Backlog

| ID | User Story | Estimate (h) | Actual (h) | Status |
|---|---|---:|---:|---|
| US-01 | As a librarian, I can record a borrowing so that the system tracks which member has which book. | 3 | TBD | DONE |
| US-02 | As a librarian, I can record a book return so that the book becomes available again. | 3 | TBD | DONE |
| US-03 | As a member, I can see my active borrowings so that I know which books I currently have. | 2 | TBD | DONE |
| US-04 | As a librarian, I can calculate the fine for an overdue book so that I can charge the right amount. (€0.50 per overdue day) | 2 | TBD | DONE |

**Sprint Goal:** Implement borrowing and returning functionality using Agile practices and Test-Driven Development.

**Total estimated effort:** 10 hours

**Actual effort:** TBD — awaiting the student's honest hours per story. Original estimates are preserved. No execution duration or AI runtime has been substituted for personal effort.

**Velocity ratio:** (US-01 actual + US-02 actual + US-03 actual + US-04 actual) / 10. Numerical value and comparison with 1.0 remain TBD until actual hours are supplied.

## Engineering tasks and verification

- US-01: resolve book/member; reject an unavailable book; transactionally mark unavailable and save a 14-day borrowing; expose POST /borrowings. Original service test and unavailable/missing-record HTTP tests pass.
- US-02: reject a repeated return; transactionally record today's return, fine, and restored availability; expose PUT /borrowings/{id}/return. Original service, endpoint, conflict, and flush/clear persistence tests pass.
- US-03: query the member's borrowings with null returnDate; expose GET /members/{id}/borrowings in BorrowingController; reject a missing member. Existing filtering/member-isolation and missing-member tests pass.
- US-04: calculate max(0, overdue days) × BigDecimal("0.50"); return BigDecimal.ZERO when not overdue. Five existing integration cases and five direct fixed-date unit cases pass.

**Story status versus lab status:** DONE describes implemented and tested story behavior. Lab 2 remains incomplete until actual hours, velocity, student reflections, final report, final commit/tag, and remote verification are complete. Latest full suite: 21 tests, 0 failures, 0 errors, 0 skipped; see reports/evidence/lab2/03-review-green.txt.
