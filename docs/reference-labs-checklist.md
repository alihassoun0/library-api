# Reference Labs 2–8 — Assignment traceability

Source: `SE_Lab_Assignment_2026_2027_Codex.docx`, document version 1.0, July 2026. Read in full before this checklist. Preserve the single existing project, Lab 1 commits/tag, original tests, actual evidence, and student ownership of decisions. The student's latest explicit instruction authorized Lab 3 despite the still-pending Lab 2 report input. Lab 3 implementation/testing/report are now prepared for final approval; Labs 4–8 remain planning only.

Every lab requires a report at the exact `reports/labN-report.md` path, a final commit on main tagged `lab-N`, artifacts committed, and a push to the existing origin. Inspect staged files before each commit. Do not invent personal hours, reflections, metrics, images, reviews, experiments, or outcomes. Record actual commands/results/errors in the active lab's evidence log. Screenshots are never a reason to stop independent work; log a precise capture checklist and use allowed tables/text alternatives.

## Lab 2 — ACTIVE; human report input pending

Objective: simulated Agile sprint; observe planning, velocity, TDD, and XP pairing. Learning: well-formed stories/task breakdown; story-driven implementation; test before code; personal estimated/actual velocity.

| Requirement | Implementation/test/artifact mapping | State |
|---|---|---|
| A: four prescribed stories, estimates before implementation | docs/sprint-backlog.md; original 3/3/2/2-hour estimates retained; engineering tasks added | Verified |
| B-1: original borrowBook_shouldMarkBookAsUnavailable test; run RED | BorrowingServiceTest; conversation compilation failure; package/setter adaptation documented | Verified conversation evidence; not falsely claimed as original RED Git commit |
| B-2: unavailable rejection, unavailable flag, saved 14-day borrowing, POST endpoint; mvn test GREEN | BorrowingService / BorrowingController; unchanged original assertions | Verified |
| C: US-02 test first, return date/availability/fine, PUT endpoint | BorrowingServiceTest, BorrowingControllerTest, BorrowingPersistenceTest; service/controller | Verified; genuine endpoint RED 3fedd09 -> GREEN b1a382a |
| C: US-03 test first; active-member query and GET mapping in BorrowingController | BorrowingRepository, BorrowingService, BorrowingController; unchanged MemberBorrowingControllerTest | Verified |
| C: US-04 pure method and direct on-time/due/1-day/7-day unit cases | BorrowingFineUnitTest (5 cases), existing BorrowingFineTest (5 integration cases) | Verified |
| C/E: actual time per story, statuses DONE | Backlog story behavior DONE; actuals TBD by explicit user instruction | Actual hours pending |
| D: prescribed senior review of errors, coverage, readability, XP | docs/lab2-xp-review.md; full output pasted into report | Recorded |
| D: apply at least two review suggestions | R1 conflict mapping/tests; R2 direct fine units; also R3 placement/R4 persistence test | Verified; review RED 078fa77 then GREEN |
| Report Q1: backlog, actual/estimated ratio, above/below 1 | reports/lab2-report.md section 1; denominator 10 hours | Student hours/numeric result pending |
| Report Q2: actual RED observation and personal effect of TDD | Report section 2; raw logs and commit facts | Personal reflection pending |
| Report Q3: full review; applied/rejected suggestions and why | Report section 3 | Student review/response pending |
| Report Q4: AI/velocity interpretation; next sprint prediction | Report section 4 | Student input pending |
| E/submission: final commit, tag, push | Exact message below; git log/tag/remote evidence | Finalization pending |

Required final commands: `mvn test`; `git add .` (inspect intended paths before staging); `git commit -m "lab2: agile sprint — borrowing feature, TDD, XP pair review"`; `git tag lab-2`; `git push origin main --tags`.

Rubric: four stories implemented and tests pass (4); Git shows at least one test before implementation (3); all report questions answered specifically (3). First two are technically verified. Final report criterion and lab-2 tag remain incomplete. The earlier Lab 3 start gate was superseded by the student's explicit later Lab 3 request; this does not declare Lab 2 complete or authorize fabricating its missing data/tag.

Latest actual suite: 21 cases, 0 failures, 0 errors, 0 skipped. Evidence: reports/evidence/lab2/01-baseline.txt (9 GREEN), 02-review-red.txt (21, 2 errors), 03-review-green.txt (21 GREEN). Screenshot checklist: docs/lab2-evidence.md. Screenshots are optional for Lab 2.

## Lab 3 — VERIFIED implementation/report; final submission approved

Objective: formal specification first; compare AI implementation with specification and identify ambiguity/gaps. Learning: precise machine-readable spec; spec before code; distinguish clarity/ambiguity; use-case acceptance criteria as test oracles.

| Step / requirement | Artifact / actual evidence | Status |
|---|---|---|
| A: complete exact template | docs/AGENTS.md: project context, three-sentence purpose, 8 rules, 5 typed fields, 3 endpoint rows, exactly 4 ACs, prohibited changes, 5 observed conventions | Verified; approved text unchanged |
| A: AC-1 exact professor message | Service and controller ac1 tests assert Book is available for direct borrowing. and HTTP409 | Passed |
| B: confirm fields/endpoints/rules before code | Complete spec and Step B plan shown; student's explicit approval of the spec and A1–A4 recorded in docs/lab3-evidence.md and report section 2 | Verified actual interaction |
| Implement approved feature | Reservation.java, ReservationRepository.java, ReservationService.java, ReservationController.java | Implemented |
| Required endpoints | POST /reservations ->201; GET /members/{memberId}/reservations ->200; DELETE /members/{memberId}/reservations/{reservationId} ->204 soft cancellation | HTTP tests pass |
| All eight rules and approved A1–A4 | Missing resources 404; available/inactive/duplicate/repeat conflicts409; generated ID/date/active; filtered lists; member scoping; no side effects; invalid requests400 | Service/HTTP tests pass |
| C: required test filenames and AC-1–AC-4 | ReservationServiceTest.java (18 cases), ReservationControllerTest.java (29 cases); four explicitly named AC methods in each | 47 new cases pass |
| C: complete mvn test including previous functionality | reports/evidence/lab3/02-implementation-tests.txt; 04-test-results.json | 68 tests, 0 failures, 0 errors, 0 skipped; original 21 pass |
| Preserve existing entities/controllers/tests/POM; no dependencies/migrations | 00-protected-files.json and 03-preservation-check.json compare 21 old files; git diff --exit-code HEAD -- src pom.xml | 0 modified protected files |
| D: observe gaps before correcting them | reports/evidence/lab3/05-gap-analysis.md; report section 1; freeze manifest06 | Recorded; no gap-driven source edits |
| Report Q1: every rule's text/classification/code evidence | reports/lab3-report.md section 1: 7 precise rules, 1 concurrency ambiguity, no missing rule definitions | Prepared for review |
| Report Q2: actual plan confirmation and clarification discussion | Report section 2 includes student path correction, approved A1–A4, actual confirmation/approval | Prepared for review |
| Report Q3: functional/non-functional classification | Report section 3 covers every spec requirement category and explains precision | Prepared for review |
| Report Q4: spec-driven vs Lab1 comparison | Report section 4 references actual Lab1 report and observed Lab3 interactions/results, without invented personal feelings | Prepared for review |
| E: final approved commit/tag/push | Student explicitly approved final submission; inspect exact staged paths and verify local/remote refs during execution | Approved; Git execution/verification follows this snapshot |

Approved submission commands: inspect/stage only intended Lab3 files; `git commit -m "lab3: reservation feature from AGENTS.md spec"`; `git tag lab-3`; `git push origin main --tags`; verify the remote commit/tag. The assignment's printed en dash in `–tags` must be corrected to Git's valid `--tags`. No old tag will be moved or created to hide missing prior submission work. Actual final Git identities/status are verified after the commit, outside this immutable submission snapshot.

Rubric evidence: complete approved spec >=5 rules/4ACs (3); implemented feature and all AC tests pass (4); specific code-referenced report gap analysis (3). Technical/report evidence is ready for student review, but final lab submission is not complete until Step E.

Evidence and optional screenshots: docs/lab3-evidence.md and reports/evidence/lab3/. No screenshot is explicitly required by Lab3, and no capture is claimed. No Lab4 work has begun.

## Lab 4 — NOT STARTED

Objective: refine design/patterns/separation and deploy a container. Learning: Repository/Service/DTO patterns; find coupling/design flaws; first-class OpenAPI; Docker build/run.

- A: review all src for controller logic, direct repository coupling, entity API exposure, missing global error handler, and OpenAPI annotations; rate CRITICAL/IMPORTANT/MINOR; student reviews findings.
- B: **BookDTO** (exact seven Book fields), **BookMapper** with toDTO/toEntity; BookController signatures use DTOs, never Book. Apply same DTO/mapper pattern to Member and Borrowing; all controllers must avoid entity responses. Preserve API behavior/tests.
- C: **GlobalExceptionHandler** with @ControllerAdvice: IllegalStateException -> 409; EntityNotFoundException or NoSuchElementException -> 404; MethodArgumentNotValidException -> 400 with field errors; other Exception -> 500. Consistent `error`/`timestamp` body. Integration tests with SpringBootTest/MockMvc prove missing-book 404 and unavailable-book 409. Preserve existing ResponseStatusException 404 behavior when introducing catch-all handling.
- D: all controllers have @Tag, endpoints @Operation and applicable @ApiResponse codes; all DTO fields @Schema/examples. Set Swagger `/swagger-ui.html`, JSON `/api-docs`. Open UI and verify every endpoint.
- E: **Dockerfile** multi-stage `maven:3.9-eclipse-temurin-17` build with `mvn package -DskipTests`, runtime `eclipse-temurin:17-jre-alpine`, port 8080, `-Xmx256m -Xms128m`; **.dockerignore**. Execute `docker build -t library-api:lab4 .`, `docker run -p 8080:8080 library-api:lab4`, `curl http://localhost:8080/books`; record actual startup/response. Missing Docker is an external prerequisite, not a claimed successful build.
- F: commit `lab4: DTOs, exception handler, OpenAPI docs, Dockerfile`, tag lab-4, push.
- **reports/lab4-report.md**: full design review and two most serious issues using design vocabulary; specific DTO/API exposure example; two design patterns/classes/problems; multistage deployment benefits and PostgreSQL changes.
- Rubric: all controllers use DTOs/no entity responses (3); global-handler tests pass (2); Swagger documents all endpoints (2); Docker image builds and starts (3).
- Initial comparison: controllers expose entities; Book/Member use repositories directly; local conflict handler is not a global handler; Springdoc dependency exists but annotations/custom JSON path, DTOs, Docker files, report/tag are missing.
- Screenshots: Swagger expanded endpoints/statuses; successful Docker build/start and curl output. Continue independent work if a human capture or Docker startup is pending.

## Lab 5 — NOT STARTED

Objective: name smells, targeted refactorings, regression evidence. Learning: named smells, Extract Method/Replace Magic Number/other patterns, tests before/after, AI technical debt.

- A: `mvn test` before edits; record baseline count/pass rate.
- B: inspect entire src/main/java; quote source evidence for at least 5 prioritized smells from the supplied list (Long Method >15 lines, Magic Numbers, Feature Envy, God Class, Duplicate Code, Primitive Obsession, Inappropriate Intimacy); select three, at least one in BorrowingService.
- C: Extract Method with before/after diff, run `mvn test`; if failure, revert that refactoring and diagnose before continuing.
- **Specification conflict to resolve with the student at this stage:** Lab 2 explicitly requires public calculateFine and direct tests; Lab 5's sample assumes an inline fine calculation and asks to extract a private method with that same signature. Our method is already extracted. Do not inline/re-extract artificially, break existing tests, or silently substitute another operation. Obtain/document an agreed truthful equivalent demonstration while preserving existing behavior.
- D: static final **DAILY_FINE_RATE** BigDecimal and **DEFAULT_LOAN_DAYS** int replace 0.50/14; `mvn test` immediately afterward.
- E: one further named smell/refactoring with rationale/diff; `mvn test` immediately afterward. Keep the whole suite green after each separate transformation.
- F: for each of 3 changes, record smell/pattern/specific before-after lines/maintenance-cost explanation.
- G: final `mvn test`; commit `lab5: three refactorings — extract method, magic numbers, [your third]` with actual third pattern, tag lab-5, push.
- **reports/lab5-report.md**: smell catalog/severity/maintainability rationale; 3 before-after logs; personal AI-debt reflection with evidence; regression safety and what each run would catch.
- Rubric: 3 distinct named smells with code evidence (3); 3 refactorings with tests before/after each (4); specific maintenance connection (3).
- Initial comparison: fine method already extracted, literals unnamed; no systematic catalog/refactoring evidence/report/tag. Do not preempt this lab's experiments now.
- Screenshots: optional baseline and each of three green summaries with diffs. Preserve separate raw outputs even if not captured.

## Lab 6 — NOT STARTED

Objective: measure/analyze quality and drive one improvement. Learning: PMD/SpotBugs, manual CC, coupling, Goal–Question–Metric.

- A: POM plugins exactly **maven-pmd-plugin 3.21.0** (`failOnViolation=false`, `printFailingErrors=true`, quickstart ruleset, targetJdk 17), **spotbugs-maven-plugin 4.8.3.1** (Max effort, Low threshold, failOnError false), **jacoco-maven-plugin 0.8.11** (agent/report so `mvn test` generates report).
- B: execute `mvn pmd:pmd`, `mvn spotbugs:spotbugs`, `mvn test jacoco:report`, `mvn pmd:check || true` (record PowerShell equivalent if needed, retain true violation exit before continuation). Expected actual outputs: **target/pmd.xml**, **target/spotbugsXml.xml**, **target/site/jacoco/index.html**. Open coverage summary and record line percentage.
- C: manual BorrowingService method CC = decision points + 1. Count the document's listed if/else-if/for/while/do/case/catch/&&/||/?; tabulate borrowBook, returnBook, calculateFine and any other methods, with lecture risk categories. Use the lecture's actual thresholds, asking for it if absent.
- D: all service classes: count imported classes Ce and importing classes Ca with a documented scope; compute **I = Ce / (Ce + Ca)** and interpret.
- E: **docs/gqm.md** with supplied Goal/Questions (2)/Metrics (2)/Results/Interpretation sections, real measurement results and actions.
- F: choose a real CRITICAL/IMPORTANT PMD/SpotBugs finding, fix, explain, rerun analysis to verify it disappeared. Never manufacture a finding or claim a nonexistent severity.
- G: `mvn test`; commit `lab6: metrics — PMD, SpotBugs, JaCoCo, cyclomatic complexity analysis`, tag lab-6, push. Preserve required analysis outputs as accessible evidence despite target being ignored.
- **reports/lab6-report.md**: JaCoCo overall/class table or screenshot and lowest class; CC and >5 discussion; coupling and most unstable role; AI vs handwritten comparison supported by at least two actual metrics (ask for authorship if unknown); completed GQM pasted.
- Rubric: real PMD/SpotBugs reports in target (2); >=40% overall coverage (2); correct CC table (3); complete measured/interpreted GQM (3).
- Initial comparison: none of the three plugins, measurement artifacts, GQM, report/tag exists. No measured coverage/CC/coupling is claimed now.
- Screenshots: JaCoCo summary (table alternative permitted), static-analysis before/after. Continue with actual XML/table evidence if screenshots pending.

## Lab 7 — NOT STARTED

Objective: structured tests, >=80% service coverage, AI test audit/evals. Learning: unit/integration/controller tests; generate then audit; coverage limits; test oracles and evals.

- A: `mvn test jacoco:report`; record baseline line/branch coverage; target >=80% service-package line coverage.
- B: comprehensive BorrowingService no-Spring MockitoExtension/@Mock suite: borrow happy/missing book/missing member/unavailable; return on-time/3 days late/missing borrowing/exact due; fine 0/1/7/negative days. Run and retain actual result. Existing tests are preserved.
- C: student manually reads/classifies generated tests Meaningful/Weak/Wrong oracle/Missing; AI also reviews and identifies 2–3 missing scenarios. Fix at least **3** real identified issues, recording before/after and rationale. Do not intentionally create meaningless tests to invent issues.
- D: despite its heading mentioning MockMvc, the actual instruction specifies **SpringBootTest(RANDOM_PORT) + TestRestTemplate**. Implement the exact three end-to-end flows: create book/member/borrow -> GET unavailable; borrow/return -> GET available; second borrow -> HTTP409.
- E: deliberate experiment 1 rate **1.00 instead of 0.50**; `mvn test`, record exactly failing/nonfailing tests; revert. Experiment 2 on-time returns **null instead of BigDecimal.ZERO**; `mvn test`, record exactly which catch it; restore correct code. Do not skip the experiment because tests already exist.
- F: `mvn test jacoco:report`; inspect uncovered lines and iterate to >=80% service coverage.
- G: final `mvn test`; commit `lab7: test suite, MockMvc integration tests, 80%+ coverage`, tag lab-7, push.
- **reports/lab7-report.md**: before/after coverage table/image; per-test audit/counts (>=10 tests); each deliberate bug's failed/passed outcomes/oracle implications; personal paragraph connecting Lab3 ACs to evals; human trust assessment with two actual audit examples.
- Rubric: all tests green (2); service >=80% line (3); meaningful audit >=10 tests (3); both deliberate bugs caught/explained (2).
- Initial comparison: existing Lab2 tests offer baseline regression coverage, not this lab's full suite/audit/coverage/deliberate experiments. None is claimed complete early.
- Screenshots: before/after JaCoCo (tables permitted), both real failing experiment summaries, final restored GREEN. Keep raw logs regardless of captures.

## Lab 8 — NOT STARTED

Objective: function points and COCOMO compared with actual effort. Learning: UFP counts, organic COCOMO81, all-lab effort comparison, AI LOC critique.

- A: inventory all external inputs/outputs/inquiries, internal logical files, external interfaces; classify simple/average/complex and justify using fields/rules. Student makes final classification. Fill supplied UFP weights EI 3/4/6, EO 4/5/7, EQ 3/4/6, ILF 7/10/15, EIF 5/7/10; sum actual weighted counts.
- B: estimated days **UFP/9**; hours **days*7.5** from stated 8–10 FP/day midpoint.
- C: all 8 labs' original estimated and actual hours, totals, ratios; honest retrospective estimates explicitly labeled if not tracked. Calculate EQF with the **DeMarco formula from the lecture**; obtain that exact formula/notes if not provided, rather than assume an alternative formula.
- D: hypothetical Fine Payment analysis (not production feature): GET /members/{id}/fines, POST /fines/{id}/pay, GET /reports/fines?month=YYYY-MM; reasoned min/best/max LOC estimates labeled estimates. Weighted size **(min+4*best+max)/6**; KLOC; **HM=2.4*KLOC^1.05**, **TDEV=2.5*HM^0.38**, **team=HM/TDEV**.
- E: execute supplied `find src/main/java -name "*.java" | xargs wc -l | tail -1` and `find src/main/java -name "*.java" | wc -l`, or document equivalent PowerShell counting semantics. Compute real LOC/hour and FP/hour with actual total hours.
- F: commit `lab8: FP estimation, COCOMO analysis, effort retrospective`, tag lab-8, push.
- **reports/lab8-report.md**: full UFP table/complexity rationale; all-lab effort table/ratios and velocity multiplier; numerical LOC vs FP critique; modified alpha supported by actual productivity calculation; student's personal lifecycle reflection. Estimates must never masquerade as measured quantities.
- Rubric: justified UFP table (3); estimated/actual hours all8 (3); correct numeric COCOMO (2); AI/lifecycle final reflection (2).
- Initial comparison: only original Lab2 story estimates currently recorded; no all-lab actuals, UFP, COCOMO, EQF, report/tag. Human effort/classification/reflections and lecture formula will be needed.
- Screenshots: optional completed calculation tables and final Git/tag/remote inventory. No image required in place of valid textual calculations.

## Final submission gate

- [ ] Preserve lab-1 and verify completed commits tagged lab-1 through lab-8.
- [ ] reports/lab1-report.md through lab8-report.md complete, with genuine human input.
- [ ] `mvn test` green on main; service JaCoCo >=80%.
- [ ] docs/AGENTS.md, docs/sprint-backlog.md with actual hours, docs/gqm.md committed.
- [ ] Dockerfile builds and container starts; Swagger /swagger-ui.html verified.
- [ ] POM includes PMD, SpotBugs, JaCoCo as specified.
- [ ] All required tags/commits pushed and remote verified; no history rewrite.
- [ ] Per-lab evidence logs and screenshot checklists identify real completed versus pending work.

Overall rubric: 10 points per lab, 80 total. A technically passing subset must never be reported as a fully complete lab.
