# Lab 7 — Software Testing

Library Management API; existing Lab1–6 project, beginning at `ce03a35`/lab-6. The complete original Lab7 assignment (all steps, deliverables and grading) was read before changes. This report answers its five questions and the user's additional strategy/overall-coverage requirements. The student has approved the final submission after presentation of the tests/audit/report. Fresh precommit verification confirms106 passing tests,96.73% overall line coverage and100% service line coverage; both deliberate defects are fully restored. Resulting Git refs are verified after execution. Lab8 is not started.

## Test strategy and required implementation

| Level | What it verifies | Implementation and reason |
|---|---|---|
| Unit, no Spring | Business rules, independent monetary oracles, exception policy, state transitions, repository interaction boundaries | New BorrowingServiceUnitTest uses MockitoExtension, three @Mock repositories and @InjectMocks. Fast, fresh fixtures/mocks per test; no database needed to isolate arithmetic and invalid-operation side effects. |
| Persistence/application integration | Actual saved relationships, return date, fine and availability | Existing BorrowingPersistenceTest flushes/clears before reloading; existing fine/reservation integration suites are preserved. These catch mappings/transactions that repository mocks cannot verify. |
| MockMvc controller integration | Routes, status, JSON DTO fields and delegation into real services/repositories | Existing HTTP/error/AC tests retained; new CatalogListControllerTest adds saved-book/member list verification. Transactional fixtures roll back; assertions locate each exact saved ID, not a global count. |
| Real HTTP end to end | Socket/server/controller/service/repository/database wiring | New BorrowingHttpIntegrationTest uses SpringBootTest(RANDOM_PORT) and TestRestTemplate for all three prescribed flows. The professor's step heading says MockMvc, but its implementation explicitly requests this real-HTTP setup. |

Each real-HTTP flow creates a fresh unique book/member through POST, never relies on another test's IDs/order, and does not pretend a test-thread transaction rolls back server-thread work. Tests assert only their own resources. Flow1 checks POST book/member/borrow then GET book available=false; flow2 checks borrow/return then GET available=true, zero non-null fine and empty active list; flow3 checks the second borrow returns409 with the correct message and still only one active borrowing. Fixtures are scoped by generated IDs rather than shared database counts.

Unit coverage includes every public BorrowingService operation: borrow happy/missing book/missing member/unavailable; return on-time/three days late/missing loan/exact due; fine0/1/7/negative days; active-member query success/missing member. The audit then adds repeat-return, empty query and a month-boundary fine. Expected values are independent strings0.00/0.50/1.50/3.50 and a14-day due date, never the production rate constant or the implementation's own output. Current-date return tests retain the existing production clock and have a possible midnight crossover limitation; fixed-date fine units avoid that issue.

## 1. Coverage before and after

Both the professor's **≥80% service-package line** target and the user's **≥80% overall line** target were exceeded at baseline. Tests were added for meaningful gaps and the required scenarios, not to force an arbitrary percentage.

| Scope | Baseline lines | Final lines | Baseline branches | Final branches |
|---|---|---|---|---|
| Overall | 290/306 =94.77% | 296/306 =96.73% | 30/38 =78.95% | 30/38 =78.95% |
| dto | 10/10 =100% | 10/10 =100% | 2/2 =100% | 2/2 =100% |
| exception | 13/13 =100% | 13/13 =100% | n/a | n/a |
| entry package | 1/3 =33.33% | 1/3 =33.33% | n/a | n/a |
| entity | 80/80 =100% | 80/80 =100% | n/a | n/a |
| service | 100/102 =98.04% | 102/102 =100% | 20/20 =100% | 20/20 =100% |
| mapper | 54/62 =87.1% | 54/62 =87.1% | 8/16 =50% | 8/16 =50% |
| controller | 32/36 =88.89% | 36/36 =100% | n/a | n/a |

| Relevant class | Before line coverage | Final line coverage |
|---|---:|---:|
| BookService | 17/18 =94.44% | 18/18 =100% |
| MemberService | 7/8 =87.50% | 8/8 =100% |
| BorrowingService | 43/43 =100% | 43/43 =100% |
| ReservationService | 33/33 =100% | 33/33 =100% |
| BookController | 10/11 =90.91% | 11/11 =100% |
| MemberController | 7/8 =87.50% | 8/8 =100% |
| BorrowingController | 6/8 =75.00% | 8/8 =100% |
| ReservationController | 9/9 =100% | 9/9 =100% |

Original uncovered service lines were BookService.java22 and MemberService.java22 (`findAll`). The catalog tests exercise those queries and verify actual returned record fields. Real-HTTP tests also execute previously uncovered BorrowingController paths. Every service class already exceeded80% initially; all four now reach100% lines. Baseline BorrowingController was below80% at class level and is now100%. The entry package/LibraryApiApplication remains33.33% (1/3), because tests start Spring contexts rather than call static main; that gap is not business logic and no artificial startup test was added solely for coverage.

Remaining10 missed lines are8 mapper null-guard return lines and2 main-entry lines. Overall branches remain30/38=78.95%, with mapper branches8/16=50%; service branches are20/20=100%. We do not claim100% overall branch coverage, or that executing every service line proves every possible behavior. No original test was deleted/weakened and no production class was excluded. Final coverage uses a fresh execution-data run (`-Djacoco.append=false`), confirmed in JaCoCo's agent arguments, after the correct source was restored; mutation runs cannot inflate the final result.

Raw before/final LINE and BRANCH counters are retained in `evidence/lab7/jacoco-before.xml` and `jacoco-final.xml`. Browsable final report: `target/site/jacoco/index.html`.

## 2. Generated test audit and actual improvements

The initial14-method Mockito suite was generated to the exact prescribed scenarios plus the additional public active-query method. It ran green before audit. The actual source is retained as `evidence/lab7/generated-suite-before-audit.java.txt`. Codex then read each test for meaningful assertions, oracle correctness, boundaries, isolation, ability to detect wrong behavior, and vacuity. This is an AI source audit presented for the student's required human review; no earlier student-authored manual audit is claimed. Approval should follow reading this table and the actual tests.

Initial classifications: **14 Meaningful,0 Weak,0 Wrong oracle**. Separately, **3 Missing scenarios** were identified: repeated return without changing/saving state; existing member with no active loans; calendar-month boundary fine. These were added and verified, making17 unit methods. The3 real-HTTP and2 catalog methods were also inspected, for **22 final generated test methods, all Meaningful**. No empty/self-equality test or deliberately wrong oracle was manufactured to produce a negative audit count. A meaningful test can still have an incomplete oracle; those weaknesses were improved as shown below.

| Generated test method | Classification | Review finding / final oracle |
|---|---|---|
| Unit: borrowAvailableBook_shouldPersistRelationshipsDatesAndAvailability | Meaningful | Checks independent14-day date, original relationships, unavailable flag, null return date and both saves; not merely the mocked returned object's identity. |
| Unit: borrowMissingBook_shouldReturn404 | Meaningful | Exact exception/status/reason; strengthened to prove no member/loan calls and no book save. |
| Unit: borrowMissingMember_shouldReturn404 | Meaningful | Correct404/reason; strengthened to preserve availability and prohibit book save/loan calls. |
| Unit: borrowUnavailableBook_shouldRejectConflict | Meaningful | Exact conflict message; strengthened to leave availability false and prohibit writes. |
| Unit: returnOnTime_shouldRecordZeroFineAndRestoreAvailability | Meaningful | Non-null numeric zero, return date, availability and persistence; detects the null experiment. |
| Unit: returnThreeDaysLate_shouldChargeOneFifty | Meaningful | Independent1.50 oracle plus state/save; detects the wrong-rate experiment. |
| Unit: returnMissingBorrowing_shouldReturn404 | Meaningful | Correct404/reason; strengthened to prevent book/member calls and any loan save. |
| Unit: returnExactlyDue_shouldChargeZero | Meaningful | Exact zero boundary, current return date and restored availability; catches null. |
| Unit: fineZeroDaysLate_shouldBeZero | Meaningful | Independent zero oracle and no repository interactions after improvement. |
| Unit: fineOneDayLate_shouldBeFiftyCents | Meaningful | Independent0.50 oracle and no repository interactions; catches rate. |
| Unit: fineSevenDaysLate_shouldBeThreeFifty | Meaningful | Independent3.50 oracle and no repository interactions; catches rate. |
| Unit: fineNegativeDays_shouldBeZero | Meaningful | No negative fine, non-null zero, no repository interactions; catches null. |
| Unit: activeBorrowings_shouldReturnRepositoryResultsForMember | Meaningful | Known member-scoped result and exact filtered-query argument; database filtering itself is verified by preserved integration tests. |
| Unit: activeBorrowingsMissingMember_shouldReturn404 | Meaningful | Missing-member404 and no borrowing query. |
| Unit: returnAlreadyReturned_shouldRejectWithoutChangingStateOrSaving | Meaningful; initially Missing | Correct conflict, unchanged date/fine/book flag and zero saves; guards against destructive repeat return. |
| Unit: activeBorrowingsExistingMemberWithoutLoans_shouldReturnEmptyList | Meaningful; initially Missing | Existing-member empty result and exact query; not a missing-member exception or null list. |
| Unit: fineAcrossMonthBoundary_shouldCountElapsedDays | Meaningful; initially Missing | Jan31->Feb1 is one day and0.50, independent of day-of-month subtraction; catches rate. |
| HTTP: createAndBorrow_shouldExposeUnavailableBookThroughRealHttp | Meaningful | Real POST chain/statuses/IDs and subsequent GET unavailable; no repository mocks. |
| HTTP: borrowAndReturn_shouldRestoreAvailabilityAndPersistZeroFineThroughRealHttp | Meaningful | Return record identity, non-null date/fine, GET available and no active loans; catches null across serialization. |
| HTTP: secondBorrow_shouldReturn409WithoutCreatingAnotherLoanThroughRealHttp | Meaningful | Exact409/message/timestamp and a single active loan with the first loan's ID. |
| MockMvc: listBooks_shouldContainPersistedBookWithItsActualFields | Meaningful | GET includes exact persisted ID/title/ISBN/unavailable flag; response shape/status alone insufficient. |
| MockMvc: listMembers_shouldContainPersistedMemberWithItsActualFields | Meaningful | GET includes exact persisted ID/name/email/inactive flag; independent of global counts. |

### Concrete before/after audit improvements

1. **Missing repeated return:** original generated suite had no case exercising the existing guard. Added a returned loan with known date/fine and asserted the exact rejection plus unchanged state/no writes. This directly tests preservation of a previous successful operation.
2. **Missing empty active list:** original suite tested only nonempty and missing-member outcomes. Added existing-member/zero-loan outcome with an empty-array/list oracle and precise repository argument.
3. **Missing calendar boundary:** original fine tests used dates within one month. Added Jan31->Feb1=0.50, which would reject an incorrect day-of-month subtraction implementation.
4. **Incomplete negative-side-effect oracles:** `borrowMissingMember_shouldReturn404` initially stopped after asserting404 and reason. It could pass if a broken service changed availability or saved the book before rejecting the request. Added `assertTrue(book.isAvailable())`, `verify(bookRepository, never()).save(...)`, and `verifyNoInteractions(borrowingRepository)`. Similar checks strengthen the other three rejection tests. This is a weakness in an otherwise Meaningful test, not a falsely labeled vacuous test.
5. **Pure-method isolation:** all four prescribed fine cases now verify no repository interactions. A numeric result alone would not detect an accidental persistence side effect.

The required new0/1/7-day fine cases overlap preserved parameterized fine tests. Their redundancy is explicit and they remain to satisfy the prescribed generated suite and independent service/unit layers; original tests are never deleted to reduce counts. The month-boundary and3-day return cases add different oracle inputs.

## 3. Deliberate bug experiments and test oracles

Correct policy: rate0.50; on-time/exact-due/early returns yield non-null BigDecimal.ZERO. Each experiment changed only the specified behavior in the existing private fine implementation/constant. A finally block restored the exact original source bytes. No deliberate defect remains in production or is included in the submission.

| State | Maven exit | Tests | Passed | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|---:|---:|
| Before experiments | 0 | 106 | 106 | 0 | 0 | 0 |
| Fine rate1.00 instead of0.50 | 1 | 106 | 98 | 8 | 0 | 0 |
| Correct source restored after rate change | 0 | 106 | 106 | 0 | 0 | 0 |
| Nonpositive-day fine null instead of ZERO | 1 | 106 | 92 | 14 | 0 | 0 |
| Correct source restored; final full suite/coverage | 0 | 106 | 106 | 0 | 0 | 0 |

Exact failing testcase identities from Surefire:

**Rate1.00 —8 failures:**

- `com.example.libraryapi.service.BorrowingFineTest.returnBook_shouldCalculateFine(String, int, BigDecimal)[3]`
- `com.example.libraryapi.service.BorrowingFineTest.returnBook_shouldCalculateFine(String, int, BigDecimal)[4]`
- `com.example.libraryapi.service.BorrowingFineUnitTest.calculateFine_shouldChargeOnlyForOverdueDays(String, LocalDate, BigDecimal)[3]`
- `com.example.libraryapi.service.BorrowingFineUnitTest.calculateFine_shouldChargeOnlyForOverdueDays(String, LocalDate, BigDecimal)[4]`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.fineAcrossMonthBoundary_shouldCountElapsedDays`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.fineOneDayLate_shouldBeFiftyCents`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.returnThreeDaysLate_shouldChargeOneFifty`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.fineSevenDaysLate_shouldBeThreeFifty`

**Zero becomes null —14 failures:**

- `com.example.libraryapi.controller.BorrowingControllerTest.returnBook_shouldReturnUpdatedBorrowingAndRestoreAvailability`
- `com.example.libraryapi.controller.BorrowingHttpIntegrationTest.borrowAndReturn_shouldRestoreAvailabilityAndPersistZeroFineThroughRealHttp`
- `com.example.libraryapi.service.BorrowingFineTest.returnBook_shouldCalculateFine(String, int, BigDecimal)[1]`
- `com.example.libraryapi.service.BorrowingFineTest.returnBook_shouldCalculateFine(String, int, BigDecimal)[2]`
- `com.example.libraryapi.service.BorrowingFineTest.returnBook_shouldCalculateFine(String, int, BigDecimal)[5]`
- `com.example.libraryapi.service.BorrowingFineUnitTest.calculateFine_shouldChargeOnlyForOverdueDays(String, LocalDate, BigDecimal)[1]`
- `com.example.libraryapi.service.BorrowingFineUnitTest.calculateFine_shouldChargeOnlyForOverdueDays(String, LocalDate, BigDecimal)[2]`
- `com.example.libraryapi.service.BorrowingFineUnitTest.calculateFine_shouldChargeOnlyForOverdueDays(String, LocalDate, BigDecimal)[5]`
- `com.example.libraryapi.service.BorrowingPersistenceTest.returnBook_shouldPersistReturnAndAvailabilityAfterClearingContext`
- `com.example.libraryapi.service.BorrowingServiceTest.returnBook_shouldMarkBookAsAvailableAndSetZeroFine`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.returnExactlyDue_shouldChargeZero`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.fineZeroDaysLate_shouldBeZero`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.fineNegativeDays_shouldBeZero`
- `com.example.libraryapi.service.BorrowingServiceUnitTest.returnOnTime_shouldRecordZeroFineAndRestoreAvailability`

For the preserved parameterized fine suites, invocation[3] means one day late and[4] seven days late; [1] is on time, [2] exactly due and[5] before due. No unsupported interpretation of a failure name is needed: these mappings match their retained CsvSource tables.

### Detections and non-detections by suite

The following table accounts for every executed test. “Pass/fail” means actual passed cases/assertion failures, not a guessed ability to detect a bug. All cases had0 errors/skips. Every individual identity, including the non-detecting cases, is retained in `evidence/lab7/experiments.json`; exhaustive failed identities also appear above.

| Suite | Cases | Rate1.00 pass/fail | Null pass/fail |
|---|---:|---:|---:|
| controller.ApiDesignContractTest | 6 | 6/0 | 6/0 |
| controller.BorrowingControllerTest | 1 | 1/0 | 0/1 |
| controller.BorrowingErrorControllerTest | 6 | 6/0 | 6/0 |
| controller.BorrowingHttpIntegrationTest | 3 | 3/0 | 2/1 |
| controller.CatalogListControllerTest | 2 | 2/0 | 2/0 |
| controller.GlobalExceptionHandlerTest | 7 | 7/0 | 7/0 |
| controller.MemberBorrowingControllerTest | 1 | 1/0 | 1/0 |
| controller.ReservationControllerTest | 29 | 29/0 | 29/0 |
| dto.ErrorResponseDTOTest | 3 | 3/0 | 3/0 |
| service.BorrowingFineTest | 5 | 3/2 | 2/3 |
| service.BorrowingFineUnitTest | 5 | 3/2 | 2/3 |
| service.BorrowingPersistenceTest | 1 | 1/0 | 0/1 |
| service.BorrowingServiceTest | 2 | 2/0 | 1/1 |
| service.BorrowingServiceUnitTest | 17 | 13/4 | 13/4 |
| service.ReservationServiceTest | 18 | 18/0 | 18/0 |

The rate defect changes positive overdue amounts only:0.50->1.00,1.50->3.00 and3.50->7.00. Eight amount-oracle tests reject it. On-time/exact-due tests correctly remain green because their result is still zero; unrelated reservation/catalog/error tests also remain green. The null defect affects the zero boundary instead: positive-overdue amount tests remain green, while14 non-null-zero service/unit/persistence/MockMvc/HTTP oracles reject it. A case that does not fail for an unrelated mutation is not automatically weak. These experiments show why both numerical and non-null/boundary assertions matter, and why line coverage alone cannot tell whether an oracle is correct.

## 4. Acceptance criteria as AI evals

The approved Lab3 acceptance criteria in docs/AGENTS.md are executable evaluation criteria for generated code: each defines an input state, action and independent expected outcome, just as an AI eval specifies a task and a scoring oracle. AC1 requires409 and the exact available-book rejection message; AC2 requires201 and saved server-controlled reservation state without changing availability; AC3 requires only the member's active records; AC4 requires204 with a retained inactive record and unchanged book state. A failing eval would be200 instead of409, a wrong message, leaking another member's reservation, or physically deleting the cancelled row. Passing compilation or a successful application start would not satisfy those criteria. The preserved named AC tests turn these specifications into repeatable evals; this lab's mutations similarly test whether assertions distinguish correct from incorrect output.

## 5. Trust in AI-generated tests

AI-generated tests need human review. Two specific audit examples support that conclusion without inventing bad tests: the initial missing-member test had the correct404 oracle but would not reject side effects before the exception; the reviewed version explicitly prohibits those writes. The initial required-scenario suite also omitted repeated return entirely; a broken repeat-return guard could therefore escape the new suite until the missing-state-preservation test was added. The audit found additional empty-list/calendar-boundary gaps and no fabricated Wrong-oracle cases. Independent expected values, layer-appropriate persistence/HTTP checks, and the observed8/14 mutation failures supply stronger evidence than a large passing test count. Codex's source audit is available for student verification; final submission approval is the human-review gate, not a claim that an AI can certify its own tests without review.

## Final grading and submission checklist

| Professor's grading criterion | Points | Verified status |
|---|---:|---|
| All tests pass | 2 | Final106/0/0/0; all84 original cases retained and passing |
| Service line coverage ≥80% | 3 | 102/102=100%; overall296/306=96.73% also passes user's target |
| Meaningful audit classification of ≥10 tests | 3 | All22 generated methods classified, initial/final counts and real improvements documented for student review |
| Both specified bugs detected and explained | 2 | Rate:8 failures; null:14 failures; exact identities and non-detections retained; correct source restored |

All original production files, POM and12 prior test files are byte-identical to the Lab6 baseline. Prior commits/tags and pending Lab4 report are preserved; missing lab-2 remains noted. No Lab8 work is included. Commit/tag/push remain pending student review and explicit approval. Exact required message: `lab7: test suite, MockMvc integration tests, 80%+ coverage`.
