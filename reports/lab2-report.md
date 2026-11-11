# Lab 2 – Agile Methodologies with AI

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Sprint Backlog and Velocity

The objective of this lab was to simulate an Agile sprint using four user stories related to the borrowing process of the Library Management API. Each story was assigned an estimated effort before implementation.

| ID | User Story | Estimated Hours | Historical Story-Level Figures (Approx.) | Status |
|---|---|---:|---:|---|
| US-01 | Record a book borrowing | 3 | 2 | Done |
| US-02 | Record a book return | 3 | 1.5 | Done |
| US-03 | View active borrowings | 2 | 1 | Done |
| US-04 | Calculate overdue fines | 2 | 1 | Done |
| **Original estimated total** | | **10** | **Not used as overall actual** | **Done** |

*Clarified total actual effort: approximately **3 hours** for Lab 2, retrospectively reconstructed from memory by the student, not measured time. The original **10-hour estimate** is unchanged. The historical story-level figures above sum to approximately5.5 hours, but the student clarified that they are not a reliable measure of total elapsed effort. They are retained only as historical figures and are not used as the lab's actual total. No new per-story allocation of the3 hours is invented.*

The four user stories were successfully implemented and tested.

The velocity ratio is calculated as follows:

**Velocity Ratio = Total Actual Hours / Total Estimated Hours**

**Actual/Estimated Effort Ratio = approximately 3 / 10 = 0.30**

The ratio is below1.0: the student's approximate retrospective total is lower than the original planned effort. This is an estimate-versus-recollection comparison, not measured time or DeMarco EQF. Review, understanding suggestions and correcting errors remain part of the work; the ratio alone does not measure an AI-caused speedup.

## 2. Test-Driven Development (TDD) Observation

Test-Driven Development was used to implement the borrowing and returning features. The process followed three stages: RED, GREEN, and REFACTOR.

First, a test was written for borrowing a book before the BorrowingService implementation existed. The test initially failed because the required class was missing. After implementing the service, the test passed.

The borrowing functionality creates a borrowing record, sets the borrowing date to the current date, sets the due date 14 days later, and marks the book as unavailable.

Additional tests were prepared for returning books, viewing active borrowings, and calculating overdue fines. The return endpoint was also tested before implementation. Its test initially returned HTTP 404 because the endpoint was missing. After implementation, the test passed.

Fine calculation was verified using five scenarios: returning on time, on the due date, one day late, seven days late, and before the due date.

The final test suite completed successfully with **21 tests passed, zero failures, and zero errors**.

Writing tests before implementation helped me understand what each feature needed to achieve. I could see exactly why a test failed and what needed to be implemented to make it pass. I learned that testing should be part of development from the beginning rather than something done only at the end.

## 3. XP Pair Programming and Code Review

A senior-style code review was performed to identify weaknesses in the borrowing implementation.

The review identified the following concerns:

| Review finding | Improvement or decision |
|---|---|
| Invalid borrowing and return operations lacked appropriate HTTP responses. | Added HTTP 409 Conflict handling and regression tests. |
| Fine calculation was tested only through the return operation. | Added five direct unit tests for calculateFine(). |
| Active borrowings were handled in MemberController instead of BorrowingController. | Moved the endpoint to the controller specified by the assignment. |
| Persistence was not independently verified after saving changes. | Added tests using flush and clear before reloading records. |
| The daily fine rate and loan duration were magic numbers. | Deferred to Lab 5, where replacing magic numbers is required. |
| Concurrent borrowing could cause inconsistent availability. | Deferred because additional locking and concurrency handling were outside this sprint's scope. |
| Date boundaries and undefined input cases needed clearer policies. | Used fixed dates in unit tests and avoided introducing unspecified business rules. |

Four improvements were applied:

1. **Error handling:** Invalid borrowing and return operations now return HTTP 409 instead of an unhandled server error.
2. **Fine calculation tests:** Direct unit tests were added to verify the fine calculation independently.
3. **Controller organization:** The active-borrowings endpoint was moved to BorrowingController.
4. **Persistence verification:** Additional tests verify saved database values after flushing and clearing the persistence context.

Other suggestions were postponed because they belong to later labs or would introduce additional complexity beyond the current requirements.

The code review helped me understand that passing tests does not automatically mean that the implementation is well designed. Reviewing the code and checking edge cases are also important parts of software development.

## 4. AI Assistance and Estimation

Codex made the implementation faster, but I still needed time to understand its suggestions, review the generated code, and fix errors.

The student's clarified retrospective estimate for the entire Lab2 is approximately3 hours, compared with the unchanged original10-hour estimate. The resulting approximate actual/estimated ratio is0.30. The older5.5-hour story sum is not used as the lab total or as a productivity denominator.

AI assistance reduced repetitive coding work and helped identify implementation problems. However, its effectiveness also depended on the quality of the instructions, the existing project structure, and the developer's ability to review and verify its output.

For the next sprint, I expect to work more efficiently because I now have a better understanding of how to write clear requirements, review proposed changes, and use tests to verify the implementation. As I become more familiar with the tools and workflow, I expect to spend less time correcting misunderstandings and more time focusing on the requirements and quality of the software.
