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
