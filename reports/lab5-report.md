# Lab 5 – Refactoring

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Code Smell Analysis

The purpose of this lab was to identify code smells in the Library Management API and improve the code structure through refactoring without changing the application's intended behavior.

The analysis covered all 29 Java source files under `src/main/java`.

Six findings were identified across three confirmed code-smell categories: Long Method, Magic Numbers, and Duplicate Code.

### 1.1 Identified Code Smells

| Code Smell | Location | Description |
|---|---|---|
| Magic Numbers | BorrowingService.java | The loan duration of 14 days and daily fine rate of €0.50 were written directly in expressions. |
| Duplicate Code | GlobalExceptionHandler.java | Six exception handlers repeated the construction of error responses. |
| Long Method | BorrowingService.java – borrowBook() | The method handled record retrieval, availability checking, and borrowing creation. |
| Long Method | BorrowingService.java – returnBook() | The method combined return validation, fine updates, book availability, and persistence. |
| Long Method | ReservationService.java – createReservation() | The method combined record retrieval, business-rule checks, and reservation creation. |
| Duplicate Code | BorrowingService.java and ReservationService.java | Similar checks for missing members were repeated. |

### 1.2 Assessment of the Seven Required Categories

The assignment required reviewing seven code-smell categories.

| Category | Finding |
|---|---|
| Long Method | Confirmed in three methods. |
| Magic Numbers | Confirmed in the loan-duration and fine-rate values. |
| Duplicate Code | Confirmed in error-response construction and member-existence checks. |
| Feature Envy | No independent problem was confirmed. |
| God Class | No class with excessive responsibilities was identified. |
| Primitive Obsession | Possible future improvement for ISBN and email, but insufficient evidence for a confirmed smell. |
| Inappropriate Intimacy | No inappropriate access to another class's internal data was identified. |

Not every category necessarily represents a problem in this application. Findings were based on actual code rather than forcing a smell into every category.

## 2. Selected Refactorings

Three code smells were selected for improvement:

1. Duplicate Code – Extract Class.
2. Magic Numbers – Replace Magic Number with Symbolic Constant.
3. Long Method – Extract Method.

### 2.1 Extract Class – Duplicate Error Handling

Before refactoring, six methods in `GlobalExceptionHandler` independently constructed error responses using `ResponseEntity` and `ErrorResponseDTO`.

This repetition meant that a future change to the error-response structure could require modifications in several places.

A new `ErrorResponseFactory` class was introduced to centralize the construction of error responses.

After refactoring:
- The six exception handlers use the shared factory.
- Exception-specific status codes and messages remain in `GlobalExceptionHandler`.
- Validation errors retain their field-specific information.
- The timestamp and error-response structure remain consistent.

This change reduced the number of separate error-envelope construction implementations from six to one.

### 2.2 Replace Magic Numbers – Borrowing Policies

Before refactoring, two important policy values were directly embedded in `BorrowingService`:

- `14` days for the default borrowing period.
- `0.50` euros as the daily overdue fine.

These values were replaced with named constants:

```java
private static final BigDecimal DAILY_FINE_RATE =
        new BigDecimal("0.50");

private static final int DEFAULT_LOAN_DAYS = 14;
```

The borrowing and fine-calculation methods now use these constants instead of repeating the values directly.

The original values and calculations were preserved.

This improvement makes the business rules easier to understand and allows policy changes to be made in clearly identified locations.

### 2.3 Extract Method – Returning a Book

The original `returnBook()` method combined several responsibilities, including retrieving the borrowing record, checking whether it had already been returned, updating the return date, calculating the fine, restoring book availability, and saving the changes.

The return-state updates were extracted into a private helper method:

`applyReturnState(Borrowing borrowing)`

The public `returnBook()` method now handles the main workflow and delegates the state updates to this helper.

The method was reduced from 19 physical lines to 11 physical lines.

The original fine-calculation method was already public and separately implemented. It was preserved to maintain compatibility with the existing tests and was adapted to delegate the calculation to a private overload.

This approach improved method organization without changing the existing public API or the fine-calculation behavior.

## 3. Maintenance Improvements

The following measurements show the structural changes made during the lab.

| Measurement | Before | After |
|---|---:|---:|
| `returnBook()` physical lines | 19 | 11 |
| Fine API method physical lines | 7 | 4, plus a 6-line private overload |
| Unnamed policy literals at use sites | 2 | 0 |
| Named policy constants | 0 | 2 |
| Error-response construction implementations | 6 | 1 |
| Exception-handler mappings | 6 | 6 |
| Production Java files | 29 | 30 |
| Production Java physical lines | 1,214 | 1,241 |

The additional source lines are due to the new factory class and extracted methods. The objective was not to reduce the total number of lines, but to improve organization and reduce duplicated responsibilities.

The refactoring also demonstrates that improving maintainability sometimes requires introducing a small additional abstraction.

### AI-Generated Code and Technical Debt

The analysis identified several maintainability issues, particularly unnamed policy values and repeated error-response construction.

These findings show that generated code still requires review and improvement. However, the project already contained service layers, DTOs, mappers, transactions, and a working regression suite.

It would therefore be incorrect to conclude that all AI-generated code is poorly designed or that AI necessarily creates more technical debt than manual development.

The improvements in this lab focused on making existing responsibilities clearer and reducing the effort required for future changes.

## 4. Testing and Verification

The complete Maven test suite was executed before and after each refactoring to verify that existing functionality remained unchanged.

The results were:

| Test Stage | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| Initial baseline | 81 | 0 | 0 | 0 |
| After Extract Class | 81 | 0 | 0 | 0 |
| After Replace Magic Number | 81 | 0 | 0 | 0 |
| After Extract Method | 81 | 0 | 0 | 0 |
| Final verification | 81 | 0 | 0 | 0 |

All five Maven runs completed successfully with `BUILD SUCCESS`.

The existing tests verified that:
- Error responses retained their correct HTTP status codes and messages.
- Fine calculations continued to produce the expected monetary values.
- The borrowing period remained 14 days.
- Returning a book correctly restored its availability.
- Database changes were successfully persisted.

The three refactorings therefore preserved the tested behavior of the application.

## Conclusion

This lab demonstrated how refactoring can improve the internal structure of an application without changing its intended functionality.

The three selected improvements reduced duplicated code, replaced unexplained numerical values with named constants, and separated return-state updates from the main borrowing workflow.

The regression tests were important throughout the process because they provided evidence that the changes did not break existing functionality.

I learned that refactoring is not simply about reducing lines of code. Its main purpose is to make software easier to understand, maintain, and modify while preserving its behavior.
