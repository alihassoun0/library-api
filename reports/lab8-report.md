# Lab 7 – Software Testing

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Introduction

The purpose of this laboratory was to improve the testing of the Library Management API and evaluate whether its automated tests could detect incorrect behavior.

The work included unit testing, integration testing, controller testing, coverage analysis, and an audit of AI-generated tests. Two deliberate defects were also introduced temporarily to evaluate the ability of the test suite to identify incorrect results.

## 2. Testing Strategy

Four testing levels were used during this laboratory.

| Testing Level | Purpose | Implementation |
|---|---|---|
| Unit Testing | Verify individual business rules and calculations. | JUnit 5 and Mockito. |
| Integration Testing | Verify database operations and persistence. | Spring Boot tests with database verification. |
| Controller Testing | Verify endpoints, HTTP status codes, and JSON responses. | MockMvc. |
| End-to-End Testing | Verify complete application workflows through real HTTP requests. | TestRestTemplate with a random application port. |

The different testing levels provide complementary verification. Unit tests isolate business logic, while integration and HTTP tests verify that the application components work together correctly.

The tests covered borrowing, returning books, fine calculations, reservations, error handling, and catalog operations.

## 3. Test Coverage Analysis

JaCoCo was used to measure code coverage before and after the testing improvements.

### 3.1 Overall Coverage

| Measurement | Before | After |
|---|---:|---:|
| Overall line coverage | 94.77% | 96.73% |
| Covered lines | 290 | 296 |
| Total executable lines | 306 | 306 |
| Overall branch coverage | 78.95% | 78.95% |
| Service line coverage | 98.04% | 100% |
| Service covered lines | 100 | 102 |

The final overall line coverage reached 96.73%, exceeding the required 80% target.

All four service classes achieved 100% line coverage:

- BookService
- MemberService
- BorrowingService
- ReservationService

The controllers also achieved 100% line coverage after the additional tests.

The application entry-point class, `LibraryApiApplication`, remained at 33.33% coverage because the Spring application-context tests did not execute its static main method.

The results show that the additional tests improved coverage without excluding production classes or weakening existing assertions.

## 4. Audit of AI-Generated Tests

The generated tests were reviewed to determine whether they contained meaningful assertions and could detect incorrect behavior.

The audit considered expected results, boundary conditions, test independence, repository interactions, and whether a test could detect incorrect application behavior.

### 4.1 Audit Results

The initial generated suite contained 14 test methods. All were classified as meaningful, but three missing scenarios were identified:

1. Repeated return of an already returned book.
2. An existing member with no active borrowings.
3. Fine calculation across a calendar-month boundary.

Additional assertions were also introduced to verify that rejected operations did not cause unwanted database changes.

The final audit covered 22 generated test methods.

### 4.2 Classification of Generated Tests

| Test | Classification | Reason |
|---|---|---|
| Borrow available book | Meaningful | Checks borrowing ID, relationships, due date, and availability. |
| Borrow missing book | Meaningful | Checks the correct 404 response and absence of writes. |
| Borrow missing member | Meaningful | Checks 404 and prevents unwanted database changes. |
| Borrow unavailable book | Meaningful | Checks conflict handling and unchanged book state. |
| Return on time | Meaningful | Verifies return date, zero fine, and restored availability. |
| Return three days late | Meaningful | Checks the independently expected €1.50 fine. |
| Return missing borrowing | Meaningful | Checks 404 and prevents unnecessary repository operations. |
| Return exactly on due date | Meaningful | Verifies the zero-fine boundary. |
| Fine for zero overdue days | Meaningful | Checks a non-null zero result. |
| Fine for one overdue day | Meaningful | Checks the €0.50 expected fine. |
| Fine for seven overdue days | Meaningful | Checks the €3.50 expected fine. |
| Fine for negative overdue days | Meaningful | Ensures an early return does not produce a negative fine. |
| Retrieve active borrowings | Meaningful | Checks the member-specific repository query. |
| Active borrowings for missing member | Meaningful | Checks the expected 404 response. |
| Return already returned book | Meaningful | Checks rejection and preservation of the existing state. |
| Existing member with empty borrowing list | Meaningful | Checks that an empty list is returned correctly. |
| Fine across month boundary | Meaningful | Verifies that January 31 to February 1 represents one overdue day. |
| Create and borrow through HTTP | Meaningful | Verifies the complete book and borrowing workflow. |
| Borrow and return through HTTP | Meaningful | Checks availability and fine information through real HTTP requests. |
| Attempt a second borrowing | Meaningful | Checks HTTP 409 and prevents duplicate active borrowing. |
| Retrieve books using MockMvc | Meaningful | Verifies the actual saved book fields in the response. |
| Retrieve members using MockMvc | Meaningful | Verifies the actual saved member fields in the response. |

**Final classification: 22 meaningful tests, 0 weak tests, and 0 wrong-oracle tests.**

The audit also identified weaknesses in some initially generated tests. For example, a missing-member test originally checked the HTTP error but did not verify that the operation had avoided unwanted database changes. Additional assertions were introduced to address this weakness.

The three missing scenarios were also added and tested successfully.

## 5. Deliberate Bug Detection Experiments

Two deliberate defects were temporarily introduced to evaluate whether the test suite could detect incorrect behavior.

The correct production implementation was restored after each experiment.

### 5.1 Experiment 1: Changing the Fine Rate

The original daily fine rate was €0.50. It was temporarily changed to €1.00.

The results were:

| Result | Count |
|---|---:|
| Total tests | 106 |
| Passed | 98 |
| Failed | 8 |
| Errors | 0 |

Eight tests detected the incorrect fine amounts.

The tests involving one-day, three-day, seven-day, and month-boundary overdue calculations failed because their expected monetary values no longer matched the implementation.

Tests involving zero fines continued to pass because changing the rate did not affect zero-fine calculations.

### 5.2 Experiment 2: Replacing Zero with Null

The implementation was temporarily modified to return `null` instead of `BigDecimal.ZERO` when there were no overdue days.

The results were:

| Result | Count |
|---|---:|
| Total tests | 106 |
| Passed | 92 |
| Failed | 14 |
| Errors | 0 |

Fourteen tests detected the incorrect zero-fine behavior.

These included unit, integration, persistence, MockMvc, and real HTTP tests.

Tests involving positive overdue fines continued to pass because their calculations were not affected by the zero-value change.

### 5.3 Final Experiment Results

| Test State | Passed | Failed | Errors |
|---|---:|---:|---:|
| Before experiments | 106 | 0 | 0 |
| Fine rate changed to 1.00 | 98 | 8 | 0 |
| Zero replaced by null | 92 | 14 | 0 |
| Final corrected implementation | 106 | 0 | 0 |

Both defects were fully restored, and no deliberate bug remained in the final production code.

These experiments demonstrated that meaningful assertions are necessary to detect incorrect behavior. High coverage alone does not guarantee that tests will identify every defect.

## 6. Acceptance Criteria and AI Evals

The acceptance criteria defined in Lab 3 serve a similar purpose to AI evaluations, commonly called evals.

Each criterion describes an expected result that can be checked independently of how the feature is implemented.

For example, the reservation feature requires the API to reject a reservation when the book is available and return HTTP 409 with the specified error message.

A failing evaluation would occur if the API returned HTTP 200 instead of HTTP 409, provided an incorrect error message, or returned reservations belonging to another member.

These criteria allow generated code to be evaluated against the original requirements rather than simply checking whether it compiles or starts successfully.

## 7. Reliability of AI-Generated Tests

AI-generated tests should not be trusted without human review.

During this laboratory, the initial test suite contained meaningful tests but still lacked important scenarios, including repeated returns and empty active-borrowing lists.

Some tests also required stronger assertions to verify that rejected operations did not modify application data.

Reviewing and improving the tests made their expected results more precise and independent of the implementation.

The deliberate bug experiments provided further evidence that the final test suite could detect incorrect fine calculations and invalid zero-fine values.

This demonstrated that AI can help generate tests, but human review remains necessary to ensure that they are accurate and capable of detecting real problems.

## Conclusion

This laboratory demonstrated the importance of evaluating both application functionality and the quality of its tests.

The final test suite contained 106 passing tests, with 96.73% overall line coverage and 100% service line coverage.

The test audit identified missing scenarios and strengthened assertions, while the deliberate bug experiments showed that the tests could detect important calculation and boundary errors.

I learned that high code coverage alone does not guarantee reliable software. Tests must contain meaningful assertions and verify the expected behavior of the application.

AI can accelerate test creation, but human review and verification remain essential to ensure software reliability.
