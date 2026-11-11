# Sprint 1 — Backlog

| ID | User Story | Estimate (h) | Historical actual (h, approximate) | Status |
|---|---|---:|---:|---|
| US-01 | As a librarian, I can record a borrowing so that the system tracks which member has which book. | 3 | 2 | DONE |
| US-02 | As a librarian, I can record a book return so that the book becomes available again. | 3 | 1.5 | DONE |
| US-03 | As a member, I can see my active borrowings so that I know which books I currently have. | 2 | 1 | DONE |
| US-04 | As a librarian, I can calculate the fine for an overdue book so that I can charge the right amount. (€0.50 per overdue day) | 2 | 1 | DONE |

**Sprint Goal:** Implement borrowing and returning functionality using Agile practices and Test-Driven Development.

**Total estimated effort:** 10 hours

**Total actual effort:** approximately **3 hours** for the entire Lab2, an approximate retrospective estimate reconstructed from the student's memory, not measured time. This is the student's clarified figure to use alongside the unchanged10-hour original estimate.

**Historical story figures:** approximately5.5 hours (2 + 1.5 + 1 + 1). The student clarified that this sum is not a reliable measure of total elapsed lab effort. Entries are preserved as historical approximate figures only;5.5 is not used as the lab actual total. No per-story redistribution of the3 hours, execution duration or AI runtime is invented.

**Actual/estimated effort ratio:** approximately3/10 = **0.30**, based on the clarified retrospective actual. This is distinct from DeMarco EQF and is not a measured productivity result.

## Engineering tasks and verification

- US-01: resolve book/member; reject an unavailable book; transactionally mark unavailable and save a 14-day borrowing; expose POST /borrowings. Original service test and unavailable/missing-record HTTP tests pass.
- US-02: reject a repeated return; transactionally record today's return, fine, and restored availability; expose PUT /borrowings/{id}/return. Original service, endpoint, conflict, and flush/clear persistence tests pass.
- US-03: query the member's borrowings with null returnDate; expose GET /members/{id}/borrowings in BorrowingController; reject a missing member. Existing filtering/member-isolation and missing-member tests pass.
- US-04: calculate max(0, overdue days) × BigDecimal("0.50"); return BigDecimal.ZERO when not overdue. Five existing integration cases and five direct fixed-date unit cases pass.

**Story status versus lab status:** DONE describes implemented and tested story behavior. Overall actual effort is now clarified as approximately3 hours and the ratio is updated consistently in the Lab2/Lab8 reports. Lab2 finalization/report review and the missing lab-2 tag remain outstanding. This update does not create or claim a final tag. Historical Lab2 suite:21 tests,0 failures,0 errors,0 skipped; see reports/evidence/lab2/03-review-green.txt. Current full-project results are recorded in the latest lab evidence.
