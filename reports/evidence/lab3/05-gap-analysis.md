# Lab 3 — Step D: specification-versus-implementation observation

Recorded after the first full implementation test run and before any gap-driven
correction. The run passed **68 tests, 0 failures, 0 errors, 0 skipped**.
No production/test changes are made to close the observations below; Step D
asks for analysis rather than silently rewriting the approved contract.

## Correct implementation of the approved behavior

| Requirement | Actual implementation and evidence | Observation |
|---|---|---|
| Rule 1 / missing records | ReservationService.reserveBook at line 33 resolves book/member; getActiveReservations at 58 verifies member; cancelReservation at 66 resolves member then scoped reservation; ReservationController.handleMissingRecord at 58 | HTTP 404 and error JSON verified for every prescribed missing-record path |
| Rule 2 / AC-1 | ReservationService lines 39–40; local conflict handler lines 51–54 | Exact available-book message and HTTP 409 verified in ac1 tests in both required classes |
| Rule 3 | ReservationService lines 42–43 | Inactive members cannot create; service/HTTP tests verify rejection and no saved record |
| Rule 4 | ReservationRepository.existsByBookIdAndMemberIdAndActiveTrue at line 11; reserveBook lines 45–46 | Sequential duplicate rejected; cancelled record allows recreation; different members can reserve same book |
| Rule 5 / AC-2 | Reservation entity lines 18–33; service lines 49–54; POST controller lines 33–36 | Generated ID, correct nested relationships, today, active=true, JSON fields and reloaded persistence verified; incoming ID/date/active cannot control the saved record |
| Rule 6 / AC-3 | Repository.findByMemberIdAndActiveTrue at line 13; service lines 57–62; GET controller lines 39–41 | Active/member filters and empty array verified without ordering assumptions |
| Rule 7 / AC-4 | Repository.findByIdAndMemberId at line 15; service lines 65–76; DELETE controller lines 44–48 | 204/no body; record remains inactive; listing excludes it; repeated cancellation 409; other member scoped 404; cancellation remains allowed when member inactive/book available |
| Rule 8 | Service never saves Book, Member, or Borrowing; new service lifecycle test compares complete JSON snapshots after flush/clear | Existing data unchanged; all 21 old tracked files/POM hash-identical and all original 21 tests pass |
| A1–A4 | Approved specification, actual approval recorded in docs/lab3-evidence.md | Soft DELETE/204, scoped 404, request 400/404 distinction, and feature exclusions implemented as approved |
| Three prohibited changes | Only new reservation source/test files; preservation manifest | No old entity/controller changes, dependencies, migration libraries, or Lab 4 work |

## Ambiguity, incompleteness, and actual choices

1. **Rule 4: concurrent duplicate requests are unspecified.** The rule precisely
   defines duplicates for normal sequential operations but does not state an
   isolation/locking policy. The implementation checks existence then saves in
   a transaction; Reservation has no database uniqueness constraint or lock.
   Concurrent creation is therefore not guaranteed to preserve uniqueness.
   The tests establish sequential behavior only; no concurrent experiment or
   stronger guarantee is claimed. Classify this rule as **ambiguous with respect
   to concurrency**, with its specified sequential cases implemented correctly.
   No locking/schema change is silently added during Step D.
2. **Multiple simultaneous errors have no prescribed priority.** reserveBook
   resolves book, member, availability, activity, then duplicate; cancel checks
   member before reservation. This is the actual code order, not a professor-
   specified precedence. Separate-invalid-condition tests do not establish a
   product requirement about which error wins when several conditions fail.
3. **Only AC-1 fixes an exact conflict message.** Other descriptive strings were
   chosen consistently ("Member is inactive.", "Member already has an active
   reservation for this book.", "Reservation is already cancelled."). HTTP
   tests enforce the approved statuses and error-field shape without treating
   these conflict strings as invented professor requirements. Missing-record
   messages mirror existing lookup conventions.
4. **Input coercion details are not a complete JSON schema.** The request uses
   Long IDs and existing Jackson/Spring binding, @Valid, and @NotNull. Missing
   IDs, malformed JSON, nonnumeric values, and nonnumeric paths are tested.
   The specification does not decide every coercible representation or unknown
   field policy. One test observes ignored supplied ID/date/active fields and
   confirms server control. No stricter parser configuration is invented.
5. **Date/timezone and deployment durability remain existing conventions.**
   LocalDate.now() follows the JVM date, as the approved text states. No fixed
   timezone/Clock, restart persistence, or concurrency SLA is specified. H2 and
   existing configuration are untouched. The tests do not prove midnight-
   rollover or restart behavior, and the report must not claim those guarantees.

## Expected versus actual and missing requirements

- No difference was found for AC-1–AC-4, the eight rules' tested sequential
  scenarios, or the approved three HTTP endpoints/statuses.
- No approved field, endpoint, or acceptance criterion is missing. The unresolved
  items above are limits of specification precision and evidence, not silently
  implemented additional functionality.
- Authentication, expiry, FIFO allocation, notifications, and automatic loans
  are deliberately excluded by approved A4; their absence is not an unmet AC.
- The requested report, final student approval, commit/tag, and remote push are
  submission tasks still pending at the time of this observation. No full lab
  completion or Git submission is claimed here.

All source references are relative to src/main/java/com/example/libraryapi;
the frozen source/test hashes are retained separately for final verification.
