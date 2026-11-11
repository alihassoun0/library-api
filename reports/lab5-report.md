# Lab 5 — Refactoring

**Project:** Library Management API

**Course:** Software Engineering (2026–2027)

**AI development tool:** OpenAI Codex CLI

Source: the complete Lab 5 section of `SE_Lab_Assignment_2026_2027_Codex.docx`, including Steps A–G, all four deliverable questions, and the 3/4/3-point grading rubric. The extracted assignment is saved in `evidence/lab5/00-assignment-lab5.txt`.

Status: all three production refactorings and their individual regression runs are complete. Step C is explicitly adapted to preserve the existing public fine API; the professor's assumed inline calculation did not exist in this baseline. The student has approved the final36-file submission, exact commit message, lab-5 tag and origin push. A fresh precommit suite again passed81 tests with zero failures/errors/skips. Resulting Git refs are verified after execution. Lab 6 has not started.

## 1. Smell catalog

The complete Step B analysis is reproduced below. Its line references describe the saved initial source, before any Lab 5 code change.

# Lab 5 — Step B code-smell analysis (before refactoring)

Scope: all 29 Java files under `src/main/java`, including the application entry point, four controllers, seven DTOs, four entities, four mappers, four repositories, four services, and the global exception handler. The numbered initial source is preserved in `02-source-before.txt`. References below are original per-file line numbers, not line numbers in that combined listing.

Abbreviated source paths below are relative to `src/main/java/com/example/libraryapi/`.

The assignment defines Long Method as more than 15 lines. Physical spans below include the declaration, closing brace, and blank lines; this is a screening measure, not a complexity score. High/medium/low ratings express likely maintenance impact, not measured defect rates. No separate lecture slides were supplied; the rationale uses the assignment's maintainability concepts: clarity, cohesion, coupling, duplicated change sites, and behavior-preserving change.

## Prioritized findings

### S1 — Magic Numbers — medium

`service/BorrowingService.java:53`: `borrowing.setDueDate(today.plusDays(14));`

`service/BorrowingService.java:92`: `return new BigDecimal("0.50").multiply(BigDecimal.valueOf(overdueDays));`

The loan duration and daily charge are domain policy embedded in expressions. Their meaning must be inferred, and policy changes require finding literals rather than named definitions. Apply **Replace Magic Number with Symbolic Constant**, using `DEFAULT_LOAN_DAYS` and `DAILY_FINE_RATE`. Zero in `overdueDays <= 0` is the natural boundary, not an additional policy constant.

### S2 — Duplicate Code: error-response construction — medium

`exception/GlobalExceptionHandler.java:27-28`, `33-34`, `39-40`, `48-49`, `54-55`, and `60-61` each build a response and construct `new ErrorResponseDTO(..., Instant.now(), ...)`.

Example: `return ResponseEntity.status(HttpStatus.CONFLICT)` followed by `.body(new ErrorResponseDTO(exception.getMessage(), Instant.now(), Map.of()));`.

Six handlers repeat the same response-envelope responsibility. A future timestamp or envelope change could be applied inconsistently. Apply **Extract Class** to a small `ErrorResponseFactory`, retaining exception selection/status/message policy in the advice. Do not merge handlers or change field-error behavior.

### S3 — Long Method: borrowing creation — medium

`service/BorrowingService.java:35-56`, 22 physical lines. It resolves two records, checks availability, updates the book, and builds/saves a borrowing.

Quotes: `Book book = bookRepository.findById(bookId)` (36); `book.setAvailable(false);` (45); `Borrowing borrowing = new Borrowing();` (49); `return borrowingRepository.save(borrowing);` (55).

Several stages must be read together to understand the workflow. **Extract Method** could name the borrowing-initialization stage without moving transaction boundaries. Medium rather than high: the flow is linear and already has focused regression tests. Deferred to avoid adding an unrelated fourth transformation.

### S4 — Long Method: returning a book — medium

`service/BorrowingService.java:59-77`, 19 physical lines. It combines lookup, repeat-return checking, date/fine recording, book availability, and persistence.

Quotes: `BigDecimal fine = calculateFine(borrowing.getDueDate(), today);` (68); `borrowing.setReturnDate(today);` (69); `book.setAvailable(true);` (73).

**Extract Method** could name the return-state update. Importantly, the fine calculation is ALREADY extracted into the public `calculateFine(LocalDate, LocalDate)` at 87-93; that seven-line method is not a Long Method. The professor's sample premise is therefore false for this baseline. Any further fine extraction must be documented as an adaptation, not presented as removal of an inline calculation from `returnBook`.

### S5 — Long Method: reservation creation — medium

`service/ReservationService.java:33-55`, 23 physical lines. It resolves records, checks three policies, constructs a reservation, and saves it.

Quotes: `if (book.isAvailable())` (39); `if (!member.isActive())` (42); `if (reservationRepository.existsByBookIdAndMemberIdAndActiveTrue(bookId, memberId))` (45); `Reservation reservation = new Reservation();` (49).

**Extract Method** could give the policy-checking stage a name. Keeping the existing check order and messages is essential to preserving observable behavior. Medium severity: the rules are cohesive and tested, but adding a policy would lengthen an already multi-stage method. Deferred.

### S6 — Duplicate Code: missing-member guard — low

`service/BorrowingService.java:81-83` and `service/ReservationService.java:59-61,67-69` repeat `if (!memberRepository.existsById(memberId)) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"); }`.

**Extract Method** inside ReservationService would remove its local duplicate; a shared lookup abstraction would be needed to remove all three copies. Low severity: three short guards, stable behavior, and extraction across services would introduce a dependency. Deferred in favor of the more frequent error-envelope duplication.

## Assessment of all seven requested categories

| Category | Assessment |
|---|---|
| Long Method | Confirmed S3-S5 against the stated >15-line screen; counts do not imply high complexity. |
| Magic Numbers | Confirmed S1: loan-duration and fine-rate policy literals. |
| Duplicate Code | Confirmed S2 and S6, with actual repeated expressions. |
| Feature Envy | No independent actionable finding. Mappers intentionally read entities; services intentionally orchestrate domain records. Getter/setter usage alone does not establish misplaced responsibility. Controllers already use services after Lab 4. |
| God Class | No substantiated finding. Services are small and organized by resource/workflow; GlobalExceptionHandler has one cross-cutting concern. File length from annotations or accessors does not establish a God Class. |
| Primitive Obsession | Possible future value-object candidates: `entity/Book.java:17` (`private String isbn;`) and `entity/Member.java:21` (`private String email;`). No duplicated parsing/validation or agreed value rules currently establish an actionable smell; do not count these as confirmed findings or add new validation. |
| Inappropriate Intimacy | No substantiated finding. Fields remain private; callers use public APIs. No access to another class's private details was found. Public accessors alone are insufficient evidence. |

## Selection and Step C discrepancy

Select S4 (Long Method: Extract Method in the return/fine workflow), S1 (Magic Numbers: Replace Magic Number), and S2 (Duplicate Code: Extract Class). Extracting the return-state update will shorten the 19-line orchestration method. The additional fine exercise refines an already-separated responsibility; the short fine method is not falsely classified as long.

The current public date-based fine method is required by Lab 2 and called by existing direct tests. The proposed adaptation preserves it and extracts its amount/zero-boundary logic into a private `calculateFine(long overdueDays)` overload. No artificial inline/re-extract cycle and no test changes are proposed. Student agreement is requested before this dependent edit. Exact resolution and execution order will be recorded in `docs/lab5-evidence.md`.

## 2. Refactoring log and measured maintenance changes

The execution order was Step E (Extract Class), Step D (Replace Magic Number), then Step C (Extract Method). Independent E/D work proceeded while the existing fine-method signature was examined. The implementation then preserved that public API and documented the Step C adaptation. Each production transformation has its own full-suite run before another transformation begins; these are three transformations, with two cohesive helper extractions in the Extract Method step.

### Extract Class — Duplicate Code (S2)

Before: six exception-handler methods each assembled `ResponseEntity` and `ErrorResponseDTO`, including `Instant.now()` and the appropriate field-error map.

After: the package-private final `ErrorResponseFactory` owns response-envelope construction. Two static `create` overloads support ordinary errors and validation field errors. The advice retains each exception annotation, status choice, message, and validation-map construction. The shared path still creates one timestamp per response and retains arbitrary `ResponseStatusException` status codes. No Spring bean or new dependency is needed.

Full before/after diff:

```diff
diff --git a/src/main/java/com/example/libraryapi/exception/GlobalExceptionHandler.java b/src/main/java/com/example/libraryapi/exception/GlobalExceptionHandler.java
index 20c6c16..8f8ce43 100644
--- a/src/main/java/com/example/libraryapi/exception/GlobalExceptionHandler.java
+++ b/src/main/java/com/example/libraryapi/exception/GlobalExceptionHandler.java
@@ -12,7 +12,6 @@ import org.springframework.web.bind.annotation.ResponseBody;
 import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
 import org.springframework.web.server.ResponseStatusException;
 
-import java.time.Instant;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.NoSuchElementException;
@@ -23,20 +22,17 @@ public class GlobalExceptionHandler {
 
     @ExceptionHandler(IllegalStateException.class)
     public ResponseEntity<ErrorResponseDTO> handleConflict(IllegalStateException exception) {
-        return ResponseEntity.status(HttpStatus.CONFLICT)
-                .body(new ErrorResponseDTO(exception.getMessage(), Instant.now(), Map.of()));
+        return ErrorResponseFactory.create(HttpStatus.CONFLICT, exception.getMessage());
     }
 
     @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
     public ResponseEntity<ErrorResponseDTO> handleNotFound(Exception exception) {
-        return ResponseEntity.status(HttpStatus.NOT_FOUND)
-                .body(new ErrorResponseDTO(exception.getMessage(), Instant.now(), Map.of()));
+        return ErrorResponseFactory.create(HttpStatus.NOT_FOUND, exception.getMessage());
     }
 
     @ExceptionHandler(ResponseStatusException.class)
     public ResponseEntity<ErrorResponseDTO> handleResponseStatus(ResponseStatusException exception) {
-        return ResponseEntity.status(exception.getStatusCode())
-                .body(new ErrorResponseDTO(exception.getReason(), Instant.now(), Map.of()));
+        return ErrorResponseFactory.create(exception.getStatusCode(), exception.getReason());
     }
 
     @ExceptionHandler(MethodArgumentNotValidException.class)
@@ -44,19 +40,16 @@ public class GlobalExceptionHandler {
         Map<String, String> fieldErrors = new LinkedHashMap<>();
         exception.getBindingResult().getFieldErrors()
                 .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
-        return ResponseEntity.badRequest()
-                .body(new ErrorResponseDTO("Validation failed", Instant.now(), fieldErrors));
+        return ErrorResponseFactory.create(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
     }
 
     @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
     public ResponseEntity<ErrorResponseDTO> handleInvalidRequest(Exception exception) {
-        return ResponseEntity.badRequest()
-                .body(new ErrorResponseDTO("Invalid request", Instant.now(), Map.of()));
+        return ErrorResponseFactory.create(HttpStatus.BAD_REQUEST, "Invalid request");
     }
 
     @ExceptionHandler(Exception.class)
     public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception exception) {
-        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
-                .body(new ErrorResponseDTO("Internal server error", Instant.now(), Map.of()));
+        return ErrorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
     }
 }
diff --git a/src/main/java/com/example/libraryapi/exception/ErrorResponseFactory.java b/src/main/java/com/example/libraryapi/exception/ErrorResponseFactory.java
new file mode 100644
index 0000000..c898dea
--- /dev/null
+++ b/src/main/java/com/example/libraryapi/exception/ErrorResponseFactory.java
@@ -0,0 +1,24 @@
+package com.example.libraryapi.exception;
+
+import com.example.libraryapi.dto.ErrorResponseDTO;
+import org.springframework.http.HttpStatusCode;
+import org.springframework.http.ResponseEntity;
+
+import java.time.Instant;
+import java.util.Map;
+
+final class ErrorResponseFactory {
+
+    private ErrorResponseFactory() {
+    }
+
+    static ResponseEntity<ErrorResponseDTO> create(HttpStatusCode status, String message) {
+        return create(status, message, Map.of());
+    }
+
+    static ResponseEntity<ErrorResponseDTO> create(
+            HttpStatusCode status, String message, Map<String, String> fieldErrors) {
+        return ResponseEntity.status(status)
+                .body(new ErrorResponseDTO(message, Instant.now(), fieldErrors));
+    }
+}
```

Maintenance effect: the error-envelope construction has one implementation instead of six. A future envelope change has one construction site to review, while exception policy remains explicit in six handlers. The extra small class is an abstraction cost; this change does not claim fewer total source lines or improved runtime performance.

Verification: baseline **81/0/0/0** (tests/failures/errors/skipped); after Extract Class **81/0/0/0**, Maven exit 0, `BUILD SUCCESS`. Raw output: `evidence/lab5/04-extract-class-tests.txt`.

### Replace Magic Number — Magic Numbers (S1)

Before: `today.plusDays(14)` and `new BigDecimal("0.50").multiply(...)` embedded the loan period and fine rate in executable expressions.

After: `private static final int DEFAULT_LOAN_DAYS = 14` and `private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50")` name both policies. Their original values and decimal representation are retained. The due-date and multiplication expressions reference these definitions.

Full before/after diff:

```diff
diff --git a/reports/evidence/lab5/05-constants-before.java.txt b/reports/evidence/lab5/05-constants-after.java.txt
index d893f58..7162c2d 100644
--- a/reports/evidence/lab5/05-constants-before.java.txt
+++ b/reports/evidence/lab5/05-constants-after.java.txt
@@ -19,6 +19,9 @@ import java.util.List;
 @Service
 public class BorrowingService {
 
+    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");
+    private static final int DEFAULT_LOAN_DAYS = 14;
+
     private final BookRepository bookRepository;
     private final MemberRepository memberRepository;
     private final BorrowingRepository borrowingRepository;
@@ -50,7 +53,7 @@ public class BorrowingService {
         borrowing.setBook(book);
         borrowing.setMember(member);
         borrowing.setBorrowDate(today);
-        borrowing.setDueDate(today.plusDays(14));
+        borrowing.setDueDate(today.plusDays(DEFAULT_LOAN_DAYS));
 
         return borrowingRepository.save(borrowing);
     }
@@ -89,6 +92,6 @@ public class BorrowingService {
         if (overdueDays <= 0) {
             return BigDecimal.ZERO;
         }
-        return new BigDecimal("0.50").multiply(BigDecimal.valueOf(overdueDays));
+        return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
     }
 }
```

Maintenance effect: two unnamed policy use sites become two named policy definitions and references. The numeric values remain in the constant definitions; they have not disappeared from the program. A maintainer can identify and change the intended policy without guessing what a literal means. Tests retain independent expected values rather than referencing the production constants.

Verification: preceding Extract Class suite **81/0/0/0**; after Replace Magic Number **81/0/0/0**, Maven exit 0, `BUILD SUCCESS`. Raw output: `evidence/lab5/05-constants-tests.txt`.

### Extract Method — Long Method: return/fine workflow (S4)

Before: `BorrowingService.returnBook` spanned original lines 59-77 (19 physical lines, 15 nonblank lines), combining lookup, the repeat-return guard, date/fine updates, book availability, and persistence. It already called the public `calculateFine(LocalDate, LocalDate)` at original lines 87-93; there was no inline formula to extract from `returnBook`.

After: `returnBook` spans final lines 62-72 (11 physical lines, 9 nonblank lines). It performs lookup, checks the repeat-return guard, delegates to private `applyReturnState(Borrowing)` at lines 74-83, and saves the borrowing. That ten-line helper contains the unchanged date/fine/availability updates. The original statement order is retained: calculate the fine, set return date and amount, restore book availability, save the book, and finally save the borrowing. The outer public method retains its transaction boundary.

For the fine-specific part of the assignment, the public date-based method remains callable by every original test. It computes overdue days and delegates to a new private `calculateFine(long overdueDays)` overload at lines 98-103. The nonpositive-day guard still returns `BigDecimal.ZERO`, and positive days still use the same decimal rate and multiplication. This is a documented adaptation, not the literal private date-signature sample: Java cannot keep public and private methods with the same signature in the same class. Making the public method private would break Lab 2's direct tests. No artificial inline/re-extract cycle or test modification was used.

An early clarification question proposed the private overload; no student response is claimed. The agent subsequently determined that preserving the API while making this reversible internal refactoring was covered by the student's explicit instruction to complete Lab 5. The concrete adaptation is presented for final submission review, rather than described as separately approved by the student or professor.

Full before/after diff against the preceding constants state:

```diff
diff --git a/reports/evidence/lab5/06-extract-method-before.java.txt b/reports/evidence/lab5/06-extract-method-after.java.txt
index 7162c2d..b7def64 100644
--- a/reports/evidence/lab5/06-extract-method-before.java.txt
+++ b/reports/evidence/lab5/06-extract-method-after.java.txt
@@ -67,6 +67,11 @@ public class BorrowingService {
             throw new IllegalStateException("Book has already been returned");
         }
 
+        applyReturnState(borrowing);
+        return borrowingRepository.save(borrowing);
+    }
+
+    private void applyReturnState(Borrowing borrowing) {
         LocalDate today = LocalDate.now();
         BigDecimal fine = calculateFine(borrowing.getDueDate(), today);
         borrowing.setReturnDate(today);
@@ -75,8 +80,6 @@ public class BorrowingService {
         Book book = borrowing.getBook();
         book.setAvailable(true);
         bookRepository.save(book);
-
-        return borrowingRepository.save(borrowing);
     }
 
     @Transactional(readOnly = true)
@@ -89,6 +92,10 @@ public class BorrowingService {
 
     public BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
         long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
+        return calculateFine(overdueDays);
+    }
+
+    private BigDecimal calculateFine(long overdueDays) {
         if (overdueDays <= 0) {
             return BigDecimal.ZERO;
         }
```

Maintenance effect: the public return operation now names its state-update stage, so lookup/guard/persistence orchestration can be read without the update details. Date-to-day conversion and day-to-amount policy have separate private/public responsibilities while preserving the established public API. The fine method was already short: the additional private overload offers a small separation benefit and does not represent removal of a previously long formula. S4's method-length finding is addressed by the return-state extraction; S3 and S5 remain deferred.

Verification: preceding Replace Magic Number suite **81/0/0/0**; after Extract Method **81/0/0/0**, Maven exit 0, `BUILD SUCCESS`. Raw output: `evidence/lab5/06-extract-method-tests.txt`.

### Structural measurements before and after

These measurements come from the actual source and saved snapshots. They describe change sites and method structure, not a monetary debt estimate or a complexity/coverage score. Physical method spans include the declaration, closing brace, and blanks; nonblank counts are shown to avoid implying that removing blank lines is the improvement.

| Measured property | Before Lab 5 | After the three refactorings | Maintenance implication |
|---|---:|---:|---|
| `returnBook` physical / nonblank lines | 19 / 15 | 11 / 9 | Named state-update stage separates orchestration from updates |
| Fine API method physical lines | 7 | 4, plus 6 in the new private overload | Explicit conversion/calculation boundary; modest benefit with an extra method |
| Unnamed policy literals at calculation/due-date use sites | 2 | 0 | Both policies are referenced by name |
| Named policy constant definitions | 0 | 2 | Values remain explicitly defined as 0.50 and 14 |
| Error-envelope construction implementations | 6 | 1 | One place to change envelope/timestamp construction |
| Exception-handler mappings | 6 | 6 | Extraction preserves separate exception policies |
| Production Java files | 29 | 30 | One small factory added |
| Total physical production Java lines | 1214 | 1241 | The design uses more lines; LOC reduction is not claimed |

Reproducible counting logic and raw results: `evidence/lab5/07-measure-structure.ps1` and `07-structural-measures.json`. The saved script was blocked by the host's default script execution policy; equivalent read-only measurement commands were run interactively without changing that policy. The earlier interim JSON records the state after E/D and is retained as such.

## 3. AI-generated debt and preventive maintenance

The reviewed code contains maintainability debt: unnamed borrowing policies (S1), six repeated error envelopes (S2), long workflow methods (S3-S5), and repeated missing-member guards (S6). Those are observable source properties, not inferred bugs. Lab 2's XP review had already identified the fine-rate/loan-period literals and deliberately deferred them to this lab. That is planned debt rather than a newly discovered failure. Lab 4 had already removed controller/repository coupling and entity responses; those historical problems must not be counted again as current smells.

For these specific parts, this review judges the unnamed domain policies and repeated error-envelope construction to be more avoidable debt than expected from a careful implementation that names policy values and centralizes a common error envelope. That is a qualitative judgment supported by S1 and S2. The code also already has service boundaries, explicit DTOs/mappers, BigDecimal arithmetic, transactions, and a passing 81-case regression suite. Its quality is mixed and reviewable; these findings do not support a general claim that AI always produces more debt than humans. No comparable independently written human implementation was measured, so a numerical AI-versus-human debt comparison would be invented.

Preventive maintenance here means addressing actual change costs before they produce inconsistent updates: name policy values, isolate return/fine responsibilities, and centralize the error envelope. The evidence shows structural changes and regression results. It does not measure saved developer hours, future defect rates, or a universal technical-debt score. Deferred smells remain documented rather than being concealed or changed outside the selected scope.

## 4. Regression safety

The command for the initial baseline and after each completed transformation was the complete `mvn test` suite. Existing test files and assertions were retained.

| Run | Tests | Failures | Errors | Skipped | Maven result |
|---|---:|---:|---:|---:|---|
| Baseline, before source edits | 81 | 0 | 0 | 0 | Exit 0; BUILD SUCCESS |
| After Extract Class | 81 | 0 | 0 | 0 | Exit 0; BUILD SUCCESS |
| After Replace Magic Number | 81 | 0 | 0 | 0 | Exit 0; BUILD SUCCESS |
| After Extract Method | 81 | 0 | 0 | 0 | Exit 0; BUILD SUCCESS |
| Final Step G verification | 81 | 0 | 0 | 0 | Exit 0; BUILD SUCCESS |

All five runs had a 100% pass rate for executed tests, with no skipped cases. Logs and suite summaries are retained separately under `evidence/lab5/`. Final verification (`09-final-verification.json`) confirms that all 11 original test files are byte-identical, the suite names/counts match the baseline, and 44 of 46 original tracked snapshots are unchanged. The only intentionally changed existing production files are BorrowingService and GlobalExceptionHandler; ErrorResponseFactory is new. The existing Lab 4 report edit, POM, Docker artifacts, source resources and original tags/history were preserved.

- **Extract Class:** `GlobalExceptionHandlerTest` checks real missing-book 404 and unavailable-book 409 flows, field errors on 400, malformed JSON, both not-found exception types, and generic 500 without internal-message leakage. Timestamp assertions parse actual UTC instants. Borrowing/reservation HTTP tests also preserve status/message behavior. These would detect many wrong statuses, lost timestamps, lost validation fields, or altered error messages.
- **Replace Magic Number:** `BorrowingServiceTest.borrowBook_shouldMarkBookAsUnavailable` independently expects the due date to be today plus 14 days. `BorrowingFineUnitTest` has five fixed-date cases for early/on-time/exact-due/one-day/seven-day outcomes and checks the `BigDecimal.ZERO` identity on zero results. Five `BorrowingFineTest` cases exercise the fine through the real return workflow. These would catch a changed loan period, wrong rate, negative charge, or broken zero boundary.
- **Extract Method:** the same direct fine tests and real return-workflow tests passed after the adaptation. `BorrowingServiceTest.returnBook_shouldMarkBookAsAvailableAndSetZeroFine` and `BorrowingControllerTest.returnBook_shouldReturnUpdatedBorrowingAndRestoreAvailability` check return dates, amounts, availability and response data. `BorrowingPersistenceTest.returnBook_shouldPersistReturnAndAvailabilityAfterClearingContext` flushes and clears the persistence context before checking stored state. These tests would detect dropped assignments or lost persistence; the fixed-date fine tests detect changed arithmetic and zero-boundary behavior. They do not prove a specific internal statement order; that order was also checked in the diff.

Passing these tests establishes compatibility for their tested cases. It does not prove behavior for every possible input, concurrency scenario, or midnight boundary, and it is not a code-coverage measurement. No Lab 6 analysis plugins or coverage work were added.

## Grading checklist and submission boundary

| Professor's criterion | Points | Evidence/status |
|---|---:|---|
| At least 3 distinct smells named with code evidence | 3 | Long Method, Magic Numbers, Duplicate Code; six confirmed findings and all seven categories assessed in section 1 |
| 3 refactorings with passing tests before and after each | 4 | Extract Class, Replace Magic Number, Extract Method; all before/after runs 81/0/0/0; Step C signature adaptation is explicit |
| Each refactoring tied to a specific maintenance improvement | 3 | Three actual patches, maintenance rationale, structural counts, tradeoffs and regression oracles in sections 2-4 |

No grade is self-awarded. Submission requires final review/approval, then the exact message `lab5: three refactorings — extract method, magic numbers, extract class`, a new `lab-5` tag, push, and remote verification. Existing `lab-1`, `lab-3`, and `lab-4` tags and all previous history must remain unchanged. The absent `lab-2` tag is not created by this lab. The pending Lab 4 report edit and unrelated assignment/startup files are excluded from the separate Lab 5 submission.
