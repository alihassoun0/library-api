# Lab 3 — Requirements and Specification

Project: the existing Library Management API. Source: the professor's
`SE_Lab_Assignment_2026_2027_Codex.docx`, Lab 3, Steps A–E and grading rubric.

**Status: implementation, tests, gap analysis, and report reviewed for submission;
the student has approved the final commit, lab-3 tag, and push. Lab 4 has not
started. Final Git results are verified during submission and reported separately.**

The objective was to write a formal specification before implementation, then
compare the output against it to identify ambiguity and gaps. This report uses
recorded interactions, source inspection, and actual test results. It does not
invent personal feelings, effort hours, screenshots, or unexecuted experiments.

## 1. Spec quality audit

The approved `docs/AGENTS.md` follows the professor's template: project context,
three-sentence purpose, eight rules, five typed fields, three endpoints, exactly
four ACs, prohibited changes, and five observed conventions. AC-1 retains the
professor's exact message; AC-2–AC-4 complete the template's placeholders using
the creation, listing, and cancellation behavior approved by the student.

The table quotes each complete business rule, with formatting compressed.
Source links identify the implementation rather than hypothetical behavior.
All eight rules have tests in the two required test classes.

| Rule text | Classification | Implementation and observed result |
|---|---|---|
| **1:** Creating a reservation requires an existing Book and Member. Return HTTP 404 if either record is missing. Listing requires an existing Member; cancellation requires an existing Member and its Reservation. | Precise enough | [ReservationService.reserveBook](../src/main/java/com/example/libraryapi/service/ReservationService.java) resolves Book/Member; getActiveReservations checks member existence; cancelReservation checks member and scoped reservation. [ReservationController.handleMissingRecord](../src/main/java/com/example/libraryapi/controller/ReservationController.java) returns the status and error JSON. Service and HTTP missing-record cases pass. |
| **2:** A member can only reserve a book that is currently unavailable (`available = false`). Otherwise return HTTP 409 with the exact message "Book is available for direct borrowing." | Precise enough | reserveBook's availability branch throws that exact message. handleConflict maps it to 409. Both `ac1_...` tests pass, and rejection saves no reservation. |
| **3:** Only active members (`active = true`) may create reservations. Reject creation by an inactive member with HTTP 409. | Precise enough | reserveBook's activity branch rejects the request; service and HTTP inactive-member tests pass. |
| **4:** A member cannot have more than one active reservation for the same book. Reject duplicate active reservations with HTTP 409. A cancelled record does not prevent a new reservation when the creation rules are satisfied. | Ambiguous about concurrency; sequential behavior precise and implemented | [ReservationRepository.existsByBookIdAndMemberIdAndActiveTrue](../src/main/java/com/example/libraryapi/repository/ReservationRepository.java) checks before save. Tests prove sequential duplicate rejection, recreation after cancellation, and different members reserving the same book. The specification gives no concurrent-request policy; no lock or database uniqueness constraint exists. Transactions alone do not guarantee concurrent uniqueness, and no such experiment was run. |
| **5:** A successful creation saves a Reservation with a generated ID, the resolved Book and Member, `reservationDate = LocalDate.now()`, and `active = true`. The server controls the generated ID, date, and active flag. | Precise enough | [Reservation](../src/main/java/com/example/libraryapi/entity/Reservation.java) defines the identity and relationships. reserveBook assigns the fields. Both `ac2_...` tests flush/clear and reload saved state; the HTTP server-control test verifies supplied ID/date/active cannot control it. |
| **6:** A member's reservation list contains only records for that member where `active = true`. Return HTTP 200 and an empty JSON array when an existing member has no active reservations. Response order is unspecified. | Precise enough | findByMemberIdAndActiveTrue implements both predicates. Both `ac3_...` tests check exact IDs/member/activity without ordering assumptions; separate empty-list tests pass. |
| **7:** Cancellation changes the member's Reservation to `active = false` without deleting its database record. Reject a repeated cancellation with HTTP 409. Cancellation does not require the member to remain active or the book to remain unavailable; those checks apply only to creation. | Precise enough after approved A1/A2 clarification | cancelReservation uses findByIdAndMemberId and sets active=false; the controller returns 204/no body. Both `ac4_...` tests confirm the retained record and exclusion from the list. Repeat/cross-member cases produce 409/404, and cancellation after member deactivation/book availability passes. |
| **8:** Creating, listing, or cancelling reservations must not change any Book availability, Member data, or Borrowing record. | Precise enough | ReservationService saves only Reservation. `reservationLifecycle_shouldNotModifyBookMemberOrBorrowingData` compares full Book/Member/Borrowing JSON snapshots after flush/clear and checks borrowing count. All old files/POM remain hash-identical and all previous 21 cases pass. |

Audit totals: **7 precise-enough rules, 1 rule with a concurrency ambiguity,
0 absent rule definitions**. These categories describe specification precision;
they do not turn the 68 passing tests into a guarantee for unspecified behavior.

Step D was recorded in
[05-gap-analysis.md](evidence/lab3/05-gap-analysis.md) after the first GREEN run
and before any gap-driven corrections. No production/test correction was made
after that observation. Additional recorded precision gaps are:

- Multiple simultaneous invalid conditions have no prescribed priority. Actual
  creation order is book, member, availability, member activity, duplicate;
  cancellation checks member before scoped reservation. That order is visible
  code behavior, not a professor-defined requirement.
- Only AC-1 prescribes an exact conflict string. Other messages are descriptive
  choices, with the approved `error` JSON field and statuses; the report does
  not attribute those strings to the professor.
- ID coercion beyond the tested invalid/missing cases is not exhaustively
  specified. Existing Spring/Jackson binding remains unchanged; no new parser
  policy was invented. Extra supplied ID/date/active fields do not override
  server-controlled values, as tested.
- The approved LocalDate.now() uses the JVM date. There is no fixed timezone,
  Clock, restart durability, or concurrency SLA. Midnight/restart guarantees
  were not tested or claimed.

No approved field, endpoint, AC, or tested sequential rule behavior was found
missing. Expected-versus-actual comparisons for AC-1–AC-4 agree. Authentication,
expiry, queues, notifications, and automatic borrowing were explicitly excluded
by approved A4, so their absence is not an unimplemented acceptance criterion.

## 2. AI as requirements partner

The initial proposal was inspected against the professor's assignment before
files were created. The student approved the eight rules and fields but changed
the cancellation interface to the member-scoped DELETE endpoint. The completed
specification then identified A1–A4 explicitly: soft DELETE/204, scoped ownership
404, invalid-request 400 versus missing-record 404, and feature exclusions.

The Step B plan confirmation shown to the student was:

> My understanding is that reservations record interest in unavailable books.
> They have five fields, start active, and are cancelled through your
> member-scoped DELETE endpoint. Creation, listing, and cancellation leave
> existing library data unchanged.
>
> After approval, I will:
>
> 1. Record your actual approval and run the existing Maven suite as the baseline.
> 2. Add the four Reservation classes using transactional services and local exception handling.
> 3. Create ReservationServiceTest.java and ReservationControllerTest.java, covering all four ACs, eight rules, and declared HTTP responses.
> 4. Run the complete suite, resolve failures, and verify existing tests remain unchanged and passing.
> 5. Record the specification-versus-code gap analysis before making any gap-driven corrections.
> 6. Prepare the four-question Lab 3 report and update the evidence/checklist. Request personal observations only where needed.
> 7. Present the final changes for separate commit, tag, and push approval.

The confirmation also listed the fields (Long id, Book book, Member member,
LocalDate reservationDate, boolean active), exact paths, 201/200/204 success
codes, eight rules, and A1–A4 in the complete AGENTS.md displayed immediately
before the plan. The actual student response was:

> I approve the complete Lab 3 specification, including decisions A1–A4 and the implementation plan.

The student explicitly authorized implementation/testing/report/checklist work
and required a final review before commit/tag. This is recorded in
[docs/lab3-evidence.md](../docs/lab3-evidence.md); no approval to commit is inferred.

Codex did ask for clarification through concrete approval of the proposed
contract details rather than inventing them silently. The DELETE replacement
was an actual student decision. A1–A3 show why an endpoint path alone leaves
important questions: whether DELETE removes history, what another member's
reservation means, and how invalid input differs from a missing resource.
Approval resolved those points before code. It did not resolve concurrency or
multi-error precedence; those are retained as Step D observations. Approval
and 68 passing cases do not imply an exhaustive specification.

## 3. Use-case connection

Functional requirements specify observable domain/API behavior or required
data. Non-functional requirements constrain implementation, compatibility,
maintainability, or verification. The following classification covers every
requirement category in the approved AGENTS.md, including its explicit
negative scope and technical conventions.

| Specification requirement | Classification | Reason |
|---|---|---|
| Purpose: record interest in borrowed books, view it, cancel it | Functional goal | Describes member use cases |
| Rule 1: resolve existing records / missing-record 404 | Functional | Defines lookup outcomes |
| Rule 2: unavailable-only reservation / exact available-book 409 | Functional | Governs eligibility and response |
| Rule 3: active member required for creation | Functional | Governs actor eligibility |
| Rule 4: reject active duplicate; allow recreation | Functional | Governs domain state transitions; concurrent scope is ambiguous |
| Rule 5: generated ID, references, today's date, active=true | Functional/data | Defines the saved/returned record |
| Rule 6: member-specific active list / empty list | Functional | Defines query behavior |
| Rule 7: cancel without physical deletion / repeated conflict | Functional | Defines cancellation behavior |
| Rule 8: other entities/borrowings unchanged | Functional invariant | Defines prohibited business side effects |
| Long id | Functional/data | Identifies a reservation |
| Book book | Functional/data | Identifies the desired book |
| Member member | Functional/data | Identifies the reserving member |
| LocalDate reservationDate | Functional/data | Records creation date |
| boolean active | Functional/data | Records cancellation state |
| reservations table, generated identity, required relationships/date, unidirectional associations | Non-functional/technical constraint | Specifies persistence representation and compatibility |
| POST /reservations, request IDs, nested JSON, 201/400/404/409 | Functional/API contract | Defines creation inputs and outcomes |
| GET /members/{memberId}/reservations, 200/400/404 | Functional/API contract | Defines member listing outcomes |
| DELETE /members/{memberId}/reservations/{reservationId}, 204/400/404/409 | Functional/API contract | Defines member-scoped cancellation outcomes |
| error JSON field and exact AC-1 message | Functional/API contract | Specifies observable response content |
| A1 soft cancellation / no body | Functional | Specifies DELETE effect and output |
| A2 wrong-member resource -> 404 | Functional | Specifies resource relationship checking |
| A3 required IDs, malformed input -> 400, absent records -> 404 | Functional | Specifies validation outcomes |
| A4 no authentication/queue/expiry/notification/automatic loans | Functional scope exclusions | Identifies behavior deliberately outside this use case |
| AC-1 available-book rejection | Functional acceptance condition | Executable oracle for Rule 2 |
| AC-2 creation fields/persistence/book unchanged | Functional acceptance condition | Executable oracle for creation |
| AC-3 active/member filtering | Functional acceptance condition | Executable oracle for listing |
| AC-4 cancellation/persistence/listing/book unchanged | Functional acceptance condition | Executable oracle for cancellation |
| Existing project, Java 17, Spring Boot 3, com.example.libraryapi | Non-functional/environment constraint | Preserves the established technology/runtime |
| No existing entity/controller changes | Non-functional/change constraint | Preserves compatibility and change scope |
| No libraries outside POM; no new dependencies | Non-functional/dependency constraint | Restricts technology additions |
| No Flyway/Liquibase | Non-functional/implementation constraint | Restricts schema tooling |
| No existing business-logic/test changes; no second project; no Lab 4 work | Non-functional/change constraint | Limits implementation scope and regression risk |
| Convention 1: packages and naming | Non-functional/maintainability | Preserves organization/readability |
| Convention 2: constructors, accessors, JPA types/repositories | Non-functional/design convention | Fits existing persistence code |
| Convention 3: constructor injection, services, write/read-only transactions | Non-functional/design and integrity constraint | Controls dependencies and update consistency |
| Convention 4: thin controller, ID record, explicit statuses, local errors/entity JSON | Non-functional/design convention | Maintains separation and existing transport style; response content is separately functional |
| Convention 5 and required test filenames/style | Non-functional/verification constraint | Defines how behavior is evidenced |
| Test all rules/statuses and preserve existing tests | Non-functional/quality assurance | Supplies regression and acceptance evidence |
| Step B approval before implementation | Non-functional/process constraint | Makes the specification a reviewed contract |

The functional rules were easier to specify as executable Given/When/Then
conditions: a particular book/member state produces a particular HTTP status
and persisted result. Technical constraints such as no dependency changes are
also objectively checkable. Broader quality goals need more precision: Rule 4's
concurrent interpretation would require explicit workload/isolation guarantees
before a concurrency acceptance test could serve as an oracle. No use-case
diagram is required by this lab; the report connects the written use cases to
the functional/non-functional distinction without inventing a diagram deliverable.

## 4. Spec-driven versus vibe-driven

This is an evidence-based comparison, not an invented account of the student's
personal feelings. Lab 1 used a detailed scaffolding prompt rather than a saved,
versioned acceptance contract. Its existing report records AI package/type/
association decisions and the student's correction of unwanted unique ISBN and
email constraints. Thus even Lab 1 was not unconstrained prompting; human
review was already necessary.

Lab 3 added a concrete artifact before implementation: eight rules, typed data,
three exact paths, error/status contracts, exclusions, four acceptance oracles,
and observed coding conventions. The student changed the cancellation path and
approved A1–A4 before production work. Those interactions demonstrate how
structured specification moves decisions earlier, where they can be reviewed
without repairing implementation afterward.

For this task, Lab 3 produced a better-controlled and more directly verifiable
feature contract. The first implementation run passed 47 reservation cases
alongside all 21 original cases; preservation hashes independently confirmed
that old source/tests/POM were unchanged. That supports the narrow conclusion
that the approved tested behavior was implemented compatibly. It does not prove
AI is generally more productive or that every untested quality is better.
The remaining concurrency/precedence gaps show that a structured specification
still requires careful requirement analysis rather than automatic trust.

## Actual verification and final grading checklist

| Test group | Cases | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| Previous Lab 1/2 regression suite | 21 | 0 | 0 | 0 |
| ReservationServiceTest | 18 | 0 | 0 | 0 |
| ReservationControllerTest | 29 | 0 | 0 | 0 |
| **Complete Maven suite** | **68** | **0** | **0** | **0** |

Command: `mvn test`. Result: **BUILD SUCCESS**, exit 0. Raw baseline and full
implementation logs are in reports/evidence/lab3/01-baseline.txt and
02-implementation-tests.txt. The first implementation run had no test failures;
no failure/fix cycle or RED history is fabricated. Test totals/case names are
extracted from actual Surefire XML into 04-test-results.json.

All four ACs have explicitly named `ac1_...` through `ac4_...` methods in both
required test classes. Nine invalid-body cases and three malformed-path cases
are parameterized HTTP tests; they are counted as cases, not separate methods.
Full-state reload and side-effect checks supplement status assertions.

| Professor's criterion | Points | Evidence/status |
|---|---:|---|
| Complete AGENTS.md, at least five rules and four ACs | 3 | Eight rules, exactly four ACs, three endpoints; all template sections complete; actual Step B approval recorded |
| Feature implemented and all AC tests pass | 4 | Four new production classes, both required test classes, 68-case complete suite GREEN |
| Specific gap analysis referencing actual code | 3 | Rule audit in section 1; method/file references, concurrency and precision observations; frozen Step D evidence |

All four assigned report questions are answered above using actual evidence.
No numeric grade is self-awarded. **Step E approval was received** after the
student reviewed the final changes, 68-case result, and grading checklist. The
approved submission uses the exact commit message
`lab3: reservation feature from AGENTS.md spec` and the lab-3 tag. Inspect the
tagged commit and origin references for the final Git identity; a commit cannot
contain its own resulting hash. lab-1 is unchanged; lab-2 is
absent from the starting repository, and no missing tag or prior completion
has been fabricated. Lab 4 is untouched.

Optional screenshot instructions and complete commands/decisions are in
docs/lab3-evidence.md. No screenshots are mandatory in this lab's rubric,
and none is claimed without an actual capture.
