# Lab 3 — Requirements and Specification: evidence log

## Status and authoritative source

**Steps A–D and the report are verified; final commit/tag/push approval RECEIVED.**
Lab 4 is out of scope. Historical entries below
describe the documentation stage at which they were recorded.

Source: `SE_Lab_Assignment_2026_2027_Codex.docx`, Lab 3, paragraphs 199–246,
including Steps A–E, the supplied AGENTS.md template, all four report questions,
and the 10-point rubric. Submission guidelines at paragraphs 77–83 were also
read. Read-only ZIP/XML extraction preserved Word line breaks; no assignment
file was edited.

Rubric: complete specification with at least five rules and four ACs (3);
implemented feature and all AC tests passing (4); specific code-referenced
report gap analysis (3). Nothing beyond the specification is claimed complete.

## Repository inspection

- Current branch: main; starting HEAD and origin/main: b2d76a3.
- Existing history includes Lab 1 and Lab 2 progress commits. lab-1 exists;
  lab-2 is absent. Lab 2's report still explicitly requires personal hours and
  reflections. The user's latest instruction authorizes beginning Lab 3;
  this does not retroactively declare Lab 2 fully submitted.
- Starting working tree: only the assignment DOCX and startup-error.txt were
  untracked. They are unrelated inputs and remain untouched.
- Existing package: com.example.libraryapi; Java 17 / Spring Boot 3.5.16;
  Spring Web, Data JPA, Validation, H2, Springdoc, and starter-test already exist.
- Book, Member, and Borrowing use generated Long identity IDs, no-argument
  constructors/setters, and LocalDate fields. Borrowing uses unidirectional
  ManyToOne associations. BorrowingService uses constructor injection and
  transactions; BorrowingController uses a request record and local 409 handler.
- Existing tests use JUnit 5, SpringBootTest, transactional isolation, and
  MockMvc. Previously retained results report 21 passing cases; **no new Maven
  run is claimed for this documentation-only step**.
- No existing Reservation class, reservation tests, or AGENTS.md was found.

Inspection commands executed: `git status --short`, `git log -10 --oneline
--decorate`, `git log -3 --oneline --decorate`, `git tag --list`, `git remote -v`,
`rg --files --hidden -g '!.git/**' -g '!target/**'`, and
`rg --files -g AGENTS.md -g '*Reservation*'`. PowerShell Get-Content inspected
existing entities, repositories, service, controllers, POM, application
configuration, tests, prior report/checklist/backlog, and Surefire summaries.

## Actual student instructions and approval

The student requested all of Lab 3, approval before significant edits and
architecture/commit/push actions, exact professor template use, no invented
evidence/reflections, and preservation of existing functionality.

After inspection, the AI proposed eight rules, five entity fields, and four
acceptance-criterion topics. The professor defines AC-1 verbatim but supplies
placeholders for AC-2/3/4; the AI explained that distinction rather than
attributing invented text to the professor.

The student's subsequent actual approval stated:

> I approve the eight proposed business rules and the Reservation entity fields.

The student replaced the proposed cancellation endpoint and specified exactly:

1. POST /reservations
2. GET /members/{memberId}/reservations
3. DELETE /members/{memberId}/reservations/{reservationId}

The student also required 404 for missing books/members/reservations, 409 for
conflicts, preservation of Book/Member/Borrowing entities/controllers, no
dependencies, and the original template. Permission was limited to creating
docs/AGENTS.md and this evidence log. Production implementation was explicitly
withheld pending the completed file and Step B plan.

## Step A changes and explicit proposed decisions

- Created docs/AGENTS.md with the original template headings, a three-sentence
  purpose, eight rules, five typed fields, exactly three endpoint rows, exactly
  four ACs, all original prohibited changes, and five observed conventions.
- Retained AC-1's exact wording/message. Filled only the professor's AC-2/3/4
  placeholders with approved creation, active listing, and cancellation topics.
- Preserved approved soft cancellation despite the endpoint changing from the
  AI's proposed PUT to the student's DELETE path.
- Explicitly labeled decisions awaiting approval: A1 DELETE returns 204/no body;
  A2 another member's reservation returns scoped 404; A3 invalid/missing request
  IDs produce 400 while nonexisting records produce 404; A4 excluded behaviors
  include authentication, queueing, expiry, notifications, and automatic loans.
- Existing source, tests, dependencies, reports, checklist, Git index/history,
  and tags were not changed in this step.

Documentation verification: reread the entire created specification using
`Get-Content docs/AGENTS.md -Encoding UTF8`. PowerShell line-pattern counts
confirmed **8 business rules, 4 acceptance criteria, and 3 endpoint rows**.
`git diff --exit-code -- src pom.xml` exited 0; tracked production/test source
and dependencies are unchanged. `git status --short` showed only the two newly
authorized documentation files plus the two pre-existing untracked inputs.
No Maven test, commit, tag, push, or screenshot was performed in this step.

## Step B plan confirmation — historical proposal before approval

**Fields:** Reservation has Long id, Book book, Member member,
LocalDate reservationDate, and boolean active; JPA table reservations;
generated ID; required unidirectional relationships/date; no inverse changes.

**Endpoints:** POST /reservations -> 201 saved JSON;
GET /members/{memberId}/reservations -> 200 active list;
DELETE /members/{memberId}/reservations/{reservationId} -> proposed 204,
persisting active=false. Missing records -> 404; conflicts -> 409; malformed
or missing required IDs -> proposed 400. AC-1 uses the exact prescribed message.

**Rules:** resolve existing records; require unavailable book/active member for
creation; reject an active duplicate; assign today/active on save; list only
the member's active records; cancel only the member-scoped record; reject a
repeat cancellation; preserve all existing book/member/borrowing data.

**Implementation plan after approval:**

1. Reread docs/AGENTS.md and record the student's real Step B approval, including
   any corrections to A1–A4. Run and retain the complete pre-change Maven baseline.
2. Add Reservation.java, ReservationRepository.java, ReservationService.java,
   and ReservationController.java in existing packages. Use constructor
   injection, write/read-only transactions, and reservation-local error handling.
   Keep existing entities/controllers, service logic, POM, and old tests intact.
3. Add ReservationServiceTest.java and ReservationControllerTest.java using the
   current SpringBootTest/transactional/MockMvc style. Trace tests to exactly
   AC-1–AC-4 and supplement with all other rule/HTTP-status cases.
4. Run mvn test, retain actual results/errors/corrections, and verify the
   previously existing 21 cases still pass. Do not fabricate counts or screenshots.
5. Observe and record the specification-versus-code gap analysis before fixing
   any newly discovered specification gaps. Classify every rule using actual
   methods/tests and describe any expected-versus-actual differences.
6. Create reports/lab3-report.md answering the four prescribed questions, paste
   this plan confirmation, distinguish actual clarification from assumptions,
   classify functional/nonfunctional requirements, and request any personal
   comparison input without inventing the student's experience. Update the
   reference checklist after its edit is approved.
7. Present the final changed/staged paths and actual test results for approval
   before committing/tagging/pushing. Use the exact lab3 commit message and
   lab-3 tag; preserve existing history/tags and verify origin afterward.

**Historical approval status at proposal: NOT YET RECEIVED.** The prior permission
applies to documentation creation only. No commit, tag, or push is authorized
by this entry; it records the requested future gate, not a fabricated approval.

## Screenshot checklist

Lab 3 does not explicitly require screenshots; these are optional useful evidence.

- [ ] Step A: editor showing all sections of docs/AGENTS.md, especially the
  eight rules and exactly four ACs.
- [ ] Step B: actual plan confirmation plus the student's subsequent approval.
- [ ] Step C: real final Maven output with test totals and BUILD SUCCESS.
- [ ] Step D: rule-by-rule report audit next to corresponding implementation/tests.
- [ ] Step E: actual git log/tag output and remote lab-3 verification after approval.

No screenshot, production implementation, new test result, completed report,
commit, or tag is claimed at this documentation stage.

## Step B actual approval and implementation execution

The next user message explicitly approved the full specification, A1–A4, and
the implementation plan:

> I approve the complete Lab 3 specification, including decisions A1–A4 and the implementation plan.

The user authorized implementation, both specified test classes, all ACs and
rules, complete-suite execution, gap analysis, report, evidence, and checklist
updates. Their approval explicitly excludes the commit/tag: final changes,
test results, and rubric checklist must be shown before those actions. No
commit, tag, push, or Lab 4 work is performed under this implementation approval.

### Commands and baseline

1. Reread the full docs/AGENTS.md and existing evidence using Get-Content with
   UTF8; inspected git status, starting HEAD, entities/repositories/configuration,
   and .gitignore. Starting HEAD: b2d76a399a6f21593171b0d3e76f7eb20c05a2e9.
2. `New-Item -ItemType Directory -Force reports/evidence/lab3 | Out-Null`.
3. `mvn test 2>&1 | Tee-Object -FilePath reports/evidence/lab3/01-baseline.txt`;
   captured Maven exit code before UTF-8 encoding normalization, then returned
   that code. **Exit 0: 21 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS.**
   Raw text is retained, including PowerShell/native stderr warning formatting.
4. `git ls-files src pom.xml` and Get-FileHash SHA256 generated
   reports/evidence/lab3/00-protected-files.json for every previously tracked
   source/configuration/test file and the POM. This is a preservation baseline,
   not a new test or a coverage metric. Get-FileHash also inspected the approved
   AGENTS.md; the approved contract text is retained unchanged.
5. `git rev-parse lab-1` confirms 452aaaf51e2aa233a3edfd047e6d9a5dfa176b03.
   No lab-2 tag exists in the current repository; none will be invented or moved.

### Implementation and testing commands/results

6. Added only Reservation.java (five fields, required JPA associations/date),
   ReservationRepository.java (active duplicate, active list, scoped lookup),
   ReservationService.java (constructor injection, transactional writes/read-only
   listing), and ReservationController.java (three exact paths, local 400/404/409
   handling). Existing POM, entities, controllers, services, and tests unchanged.
7. Added the two specified test classes. ReservationServiceTest has 18 database-
   backed cases using the existing SpringBootTest/transactional style.
   ReservationControllerTest has 29 MockMvc cases, including 9 invalid-body and
   3 malformed-path parameterized invocations. Each class explicitly names four
   AC methods; all 8 rules are mapped in the report/checklist and Step D table.
8. `mvn test 2>&1 | Tee-Object -FilePath reports/evidence/lab3/02-implementation-tests.txt`;
   preserved actual exit status and normalized only the log encoding to UTF-8.
   **Exit 0: 68 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS.** Original
   21 cases, new service18, new controller29 all pass on the first implementation
   run. No test-failure/correction cycle, RED result, or additional run is invented.
9. Compared each file in 00-protected-files.json with current SHA256 and saved
   03-preservation-check.json: **21 protected files checked; 0 changed**.
   `git diff --exit-code HEAD -- src pom.xml` exited0. Approved AGENTS.md hash is
   still 663428DFD17E04A7B9AF1765E77B6CAC364B78B298253373CCD3972A4117D64B.
10. Parsed actual target/surefire-reports/TEST-*.xml with PowerShell XML into
    04-test-results.json (class totals and testcase outcomes). Inspected source
    with line numbers and test method names using Get-Content and rg. Read the
    existing reports/lab1-report.md for evidence-based comparison.
11. Recorded Step D in reports/evidence/lab3/05-gap-analysis.md before any gap-
    driven edits. Seven rules precise, Rule4 ambiguous about concurrency, no
    missing rule definitions. No accepted sequential AC behavior differs from
    the test result. Noted unprescribed multi-error precedence, non-AC1 message
    strings, coercion details, and timezone/restart/concurrency limits. Did not
    silently fix these observations or expand scope.
12. Captured hashes for all four new production and two test classes in
    06-source-test-manifest.json after the Step D observation. Final checks
    compare these to show no source changes were made after analysis.
13. Created reports/lab3-report.md with all four required questions answered:
    complete rule texts/classification/method evidence; actual plan and approval/
    clarification discussion; full functional/nonfunctional classification;
    evidence-based Lab1-versus-Lab3 comparison. No student feelings, effort, or
    screenshots are invented. Updated docs/reference-labs-checklist.md with
    actual evidence/status, retaining Lab2's pending report/tag status and
    recording that the student's later Lab3 instruction superseded the old gate.
14. Generated reports/evidence/lab3/07-final-verification.json from actual file
    hashes, the approved specification, and Surefire-derived totals. Results:
    21 old files unchanged; 0 new source/test changes since gap analysis; approved
    spec hash unchanged; 8 rules, 4 ACs, 3 endpoints; 68 cases, 0 failures/errors/
    skipped; 0 trailing-whitespace findings in new source/tests/documentation
    (raw command output excluded); no staged files; no lab-3 tag; HEAD unchanged.
    `git diff --check` passed for tracked changes. `git status --short` confirms
    only the intended Lab3 additions/checklist update plus the two pre-existing
    untracked inputs. `rg` confirms all four numbered report sections, all eight
    rule audit rows, and four explicitly named AC methods in each test class.

### Final files for student approval

- New production: entity/Reservation.java, repository/ReservationRepository.java,
  service/ReservationService.java, controller/ReservationController.java under
  src/main/java/com/example/libraryapi.
- New tests: service/ReservationServiceTest.java and
  controller/ReservationControllerTest.java under src/test/java/com/example/libraryapi.
- New specification/evidence: docs/AGENTS.md and docs/lab3-evidence.md.
- New required report: reports/lab3-report.md (four complete assigned sections).
- Modified tracking file: docs/reference-labs-checklist.md.
- New supporting evidence: reports/evidence/lab3/00-protected-files.json,
  01-baseline.txt, 02-implementation-tests.txt, 03-preservation-check.json,
  04-test-results.json, 05-gap-analysis.md, 06-source-test-manifest.json,
  07-final-verification.json.
- Exclude from staging: SE_Lab_Assignment_2026_2027_Codex.docx and startup-error.txt.
  They were present before the Lab3 work and are unchanged.

### Error/correction record

No compilation, startup, or test errors occurred in the two actual Maven runs.
The JVM class-sharing warning appears in PowerShell's stderr formatting as
NativeCommandError text; Maven still exited0. It is not counted as a test error.
No dependencies were downloaded or changed for the feature. No source fixes
were needed after the first GREEN run, and no earlier failure is fabricated.

### Current test inventory

| Test class/group | Cases | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| Previous BorrowingControllerTest | 1 | 0 | 0 | 0 |
| Previous BorrowingErrorControllerTest | 6 | 0 | 0 | 0 |
| Previous MemberBorrowingControllerTest | 1 | 0 | 0 | 0 |
| Previous BorrowingFineTest | 5 | 0 | 0 | 0 |
| Previous BorrowingFineUnitTest | 5 | 0 | 0 | 0 |
| Previous BorrowingPersistenceTest | 1 | 0 | 0 | 0 |
| Previous BorrowingServiceTest | 2 | 0 | 0 | 0 |
| New ReservationServiceTest | 18 | 0 | 0 | 0 |
| New ReservationControllerTest | 29 | 0 | 0 | 0 |
| **Total** | **68** | **0** | **0** | **0** |

### Submission gate

The final student approval was received after the implementation review:

> I approve the Lab 3 final submission.

The student explicitly requested inspection of staged files, the exact required
commit message, lab-3 on that commit, push to origin, remote commit/tag
verification, and preservation of lab-1/history. This authorizes those actions;
it does not authorize Lab4. Earlier pending-approval entries document the actual
state at that earlier stage rather than an approval that had not yet occurred.

Approved submission sequence:
The exact future commit is `lab3: reservation feature from AGENTS.md spec`;
future tag lab-3. `git push origin main --tags` uses a real double hyphen in
place of the en dash printed in the assignment. No command in this paragraph
has been run. No existing tag/history has been changed. Only Lab3 is active.

Screenshot opportunities remain optional: capture the approved specification
and approval interaction, the actual terminal/log summary with 68 GREEN cases,
the report's rule/code audit, and the final Git/tag/remote output after approval.
Saved text logs can be opened now if the live terminal has scrolled. No pending
screenshot prevents completion of independent work.

### Final approval inspection

Before staging, `git status --short`, `git diff --cached --name-only`,
`git log -8 --oneline --decorate`, `git remote -v`, and `git show-ref --tags`
confirmed starting HEAD b2d76a3, an empty index, existing origin, and lab-1 at
452aaaf51e2aa233a3edfd047e6d9a5dfa176b03. The exact two unrelated inputs
SE_Lab_Assignment_2026_2027_Codex.docx and startup-error.txt are excluded.
Read the final verification JSON and evidence directory inventory. Only
submission-approval status text in this report/evidence/checklist was updated;
no production code or tests changed after the 68-case GREEN result.

The submitted snapshot contains the real approval and precommit evidence.
Final commit hash, tag, remote refs, and ancestor checks are necessarily
verified after creating that commit. Their actual outputs will be provided in
the final response and saved locally as target/lab3-submission-verification.json
(generated verification output, not a fabricated pre-existing result).
