# Lab 7 – Software Testing

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Introduction

The objective of this laboratory was to improve the testing of the Library Management API and evaluate whether its automated tests could detect incorrect behavior.

The testing process included unit tests, integration tests, and HTTP tests using MockMvc and TestRestTemplate. The test suite was also reviewed to identify missing scenarios, weak assertions, and improvements to test quality.

## 2. Testing Strategy

Four testing approaches were used.

| Testing Level | Purpose |
|---|---|
| Unit Testing | Tests individual service methods and business rules independently using Mockito. |
| Integration Testing | Verifies database operations, relationships, transactions, and persistence. |
| Controller Testing | Uses MockMvc to verify endpoints, HTTP status codes, and JSON responses. |
| End-to-End Testing | Uses TestRestTemplate to test complete HTTP workflows against a running application. |

The different levels provide complementary verification. Unit tests isolate business logic, while integration and HTTP tests verify that the application components work together correctly.

The tests covered borrowing, returning books, fine calculations, reservation behavior, error handling, and catalog operations.

## 3. Test Coverage Analysis

JaCoCo was used to measure code coverage before and after the testing improvements.

### 3.1 Overall Coverage

| Measurement | Before | After |
|---|---:|---:|
| Overall line coverage | 94.77% | 96.73% |
| Covered lines | 290 | 296 |
| Total executable lines | 306 | 306 |
| Service line coverage | 98.04% | 100% |
| Service covered lines | 100 | 102 |
| Service total lines | 102 | 102 |
| Branch coverage | 78.95% | 78.95% |

The final overall coverage reached 96.73%, exceeding the required 80% target.

All four service classes achieved 100% line coverage:

- BookService
- MemberService
- BorrowingService
- ReservationService

The lowest-covered class was `LibraryApiApplication`, with 33.33% coverage. Its main method was not executed by the Spring application-context tests. This did not indicate a problem with the application's business logic.

The remaining uncovered lines were mainly in mapper null-handling branches and the application entry point.

## 4. Audit of AI-Generated Tests

The generated tests were reviewed to determine whether they contained meaningful assertions and could detect incorrect behavior.

The audit considered test isolation, expected results, boundary conditions, repository interactions, and the ability to identify incorrect outputs.

### 4.1 Audit Results

The initial generated suite contained 14 test methods. All were classified as meaningful, but three missing scenarios were identified.

The following additional tests were introduced:

1. Repeated return: verifies that returning an already returned book does not modify its existing state.
2. Empty active-borrowing list: verifies that an existing member without active borrowings receives an empty result.
3. Month-boundary fine calculation: verifies that a return from January 31 to February 1 is calculated as one overdue day.

Additional assertions were also introduced to ensure that invalid operations do not cause unwanted database changes.

The final audit covered 22 generated test methods, all classified as meaningful.

### 4.2 Examples of Test Improvements

**Example 1: Missing member**

The initial test checked the expected HTTP error but did not verify whether the operation caused unwanted side effects.

The improved test checks that the book remains available and that no borrowing record is saved.

**Example 2: Repeated return**

The original suite did not contain a dedicated test for returning the same book twice.

A new test verifies that the operation is rejected and that the existing return date, fine amount, and book availability remain unchanged.

These examples demonstrate that a test must verify not only the expected response but also the state of the application.

## 5. Deliberate Bug Detection Experiments

Two deliberate defects were introduced temporarily to evaluate the quality of the test suite.

The correct production implementation was restored after each experiment.

### 5.1 Experiment 1: Changing the Fine Rate

The original daily fine rate was €0.50.

It was temporarily changed to €1.00.

The result was:

- Total tests: 106
- Passed: 98
- Failed: 8

The failing tests detected incorrect fine amounts for overdue borrowing records.

Tests involving zero fines continued to pass because the zero-fine behavior was not affected by the rate change.

### 5.2 Experiment 2: Replacing Zero with Null

The implementation was temporarily modified to return `null` instead of `BigDecimal.ZERO` when there were no overdue days.

The result was:

- Total tests: 106
- Passed: 92
- Failed: 14

The failing tests detected the incorrect zero-fine result in service, persistence, and HTTP responses.

Tests involving positive overdue fines continued to pass because their calculations were not affected by this change.

### 5.3 Experiment Results

| Experiment | Passed | Failed | Errors |
|---|---:|---:|---:|
| Fine rate changed to 1.00 | 98 | 8 | 0 |
| Zero replaced by null | 92 | 14 | 0 |
| Final corrected implementation | 106 | 0 | 0 |

Both defects were fully restored, and no deliberate bug remained in the final production code.

The experiments demonstrated that meaningful assertions are necessary to detect incorrect behavior. A test suite may have high coverage while still missing important defects.

## 6. Acceptance Criteria and AI Evals

The acceptance criteria defined in Lab 3 serve a similar purpose to AI evaluations, or evals.

Each criterion defines an expected behavior that can be checked independently of the implementation.

For example, the reservation feature requires the API to reject a reservation when the book is available, return the correct HTTP status, and provide the specified error message.

A failing evaluation would occur if the API returned HTTP 200 instead of HTTP 409, returned an incorrect message, or allowed a cancelled reservation to remain active.

These criteria make it possible to evaluate whether AI-generated code satisfies the original requirements rather than simply checking whether it compiles or runs.

## 7. Reliability of AI-Generated Tests

AI-generated tests should not be trusted without human review.

During this laboratory, the initial test suite contained meaningful tests but still lacked important scenarios, including repeated returns and empty active-borrowing lists.

Some tests also needed stronger assertions to verify that rejected operations did not modify application data.

Reviewing and improving the tests helped make the expected results more precise and independent of the implementation.

The two deliberate bug experiments provided further evidence that the final test suite could detect incorrect fine calculations and invalid zero-fine values.

This demonstrated that human review remains necessary when working with AI-generated tests.

## Conclusion

This laboratory demonstrated the importance of testing both application functionality and the quality of the tests themselves.

The final test suite contained 106 passing tests, with 96.73% overall line coverage and 100% service line coverage.

The test audit identified missing scenarios and strengthened assertions, while the deliberate bug experiments showed that the tests could detect important calculation and boundary errors.

I learned that high code coverage alone does not guarantee reliable software. Tests must contain meaningful assertions and verify the expected behavior of the application.

AI can help generate tests, but human review is necessary to ensure that those tests are accurate, independent, and capable of detecting real problems.
