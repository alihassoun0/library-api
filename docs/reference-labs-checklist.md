# Reference Labs 2–8 — Assignment traceability

Source: `SE_Lab_Assignment_2026_2027_Codex.docx`, document version 1.0, July 2026. Read in full before this checklist. Preserve the single existing project, existing commits/tags, original tests, actual evidence, and student ownership of decisions. Lab3 was submitted at2d5a1d4/tag lab-3; Lab4 at d159267/lab-4; Lab5 at822e381/lab-5; Lab6 atce03a35/lab-6; Lab7 ataffff78/lab-7. The revised Lab4 report remains a separate pending change. The current instruction authorizes Lab8 estimation/report work; commit/tag/push require final review and approval. Lab2's missing final lab-2 tag remains outstanding.

Every lab requires a report at the exact `reports/labN-report.md` path, a final commit on main tagged `lab-N`, artifacts committed, and a push to the existing origin. Inspect staged files before each commit. Do not invent personal hours, reflections, metrics, images, reviews, experiments, or outcomes. Record actual commands/results/errors in the active lab's evidence log. Screenshots are never a reason to stop independent work; log a precise capture checklist and use allowed tables/text alternatives.

## Lab 2 — finalization pending; approximately3h actual clarified, lab-2 tag missing

Objective: simulated Agile sprint; observe planning, velocity, TDD, and XP pairing. Learning: well-formed stories/task breakdown; story-driven implementation; test before code; personal estimated/actual velocity.

| Requirement | Implementation/test/artifact mapping | State |
|---|---|---|
| A: four prescribed stories, estimates before implementation | docs/sprint-backlog.md; original 3/3/2/2-hour estimates retained; engineering tasks added | Verified |
| B-1: original borrowBook_shouldMarkBookAsUnavailable test; run RED | BorrowingServiceTest; conversation compilation failure; package/setter adaptation documented | Verified conversation evidence; not falsely claimed as original RED Git commit |
| B-2: unavailable rejection, unavailable flag, saved 14-day borrowing, POST endpoint; mvn test GREEN | BorrowingService / BorrowingController; unchanged original assertions | Verified |
| C: US-02 test first, return date/availability/fine, PUT endpoint | BorrowingServiceTest, BorrowingControllerTest, BorrowingPersistenceTest; service/controller | Verified; genuine endpoint RED 3fedd09 -> GREEN b1a382a |
| C: US-03 test first; active-member query and GET mapping in BorrowingController | BorrowingRepository, BorrowingService, BorrowingController; unchanged MemberBorrowingControllerTest | Verified |
| C: US-04 pure method and direct on-time/due/1-day/7-day unit cases | BorrowingFineUnitTest (5 cases), existing BorrowingFineTest (5 integration cases) | Verified |
| C/E: actual time per story, statuses DONE | Historical approximate2/1.5/1/1h story sum retained only as history; student clarified approximately3h overall actual from memory | Overall actual resolved; no invented per-story allocation; DONE behavior retained |
| D: prescribed senior review of errors, coverage, readability, XP | docs/lab2-xp-review.md; full output pasted into report | Recorded |
| D: apply at least two review suggestions | R1 conflict mapping/tests; R2 direct fine units; also R3 placement/R4 persistence test | Verified; review RED 078fa77 then GREEN |
| Report Q1: backlog, actual/estimated ratio, above/below 1 | Lab2 report/backlog updated to approximately3h actual against unchanged10h estimate; approximate ratio0.30 | Clarified consistently; final report review/tag pending |
| Report Q2: actual RED observation and personal effect of TDD | Report section 2; raw logs and commit facts | Personal reflection pending |
| Report Q3: full review; applied/rejected suggestions and why | Report section 3 | Student review/response pending |
| Report Q4: AI/velocity interpretation; next sprint prediction | Report section 4 | Student input pending |
| E/submission: final commit, tag, push | Exact message below; git log/tag/remote evidence | Finalization pending |

Required final commands: `mvn test`; `git add .` (inspect intended paths before staging); `git commit -m "lab2: agile sprint — borrowing feature, TDD, XP pair review"`; `git tag lab-2`; `git push origin main --tags`.

Rubric: four stories implemented and tests pass (4); Git shows at least one test before implementation (3); all report questions answered specifically (3). First two are technically verified. Final report criterion and lab-2 tag remain incomplete. The earlier Lab 3 start gate was superseded by the student's explicit later Lab 3 request; this does not declare Lab 2 complete or authorize fabricating its missing data/tag.

Latest actual suite: 21 cases, 0 failures, 0 errors, 0 skipped. Evidence: reports/evidence/lab2/01-baseline.txt (9 GREEN), 02-review-red.txt (21, 2 errors), 03-review-green.txt (21 GREEN). Screenshot checklist: docs/lab2-evidence.md. Screenshots are optional for Lab 2.

## Lab 3 — SUBMITTED at 2d5a1d4 / lab-3

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
| E: final approved commit/tag/push | Student approved;18 staged files inspected; exact commit2d5a1d4 and tag lab-3 pushed and remote refs verified in the actual submission turn | Completed; lab-1 and8 prior commits preserved |

Approved submission commands: inspect/stage only intended Lab3 files; `git commit -m "lab3: reservation feature from AGENTS.md spec"`; `git tag lab-3`; `git push origin main --tags`; verify the remote commit/tag. The assignment's printed en dash in `–tags` must be corrected to Git's valid `--tags`. No old tag will be moved or created to hide missing prior submission work. Actual final Git identities/status are verified after the commit, outside this immutable submission snapshot.

Rubric evidence: complete approved spec >=5 rules/4ACs (3); implemented feature and all AC tests pass (4); specific code-referenced report gap analysis (3). Technical/report evidence is ready for student review, but final lab submission is not complete until Step E.

Evidence and optional screenshots: docs/lab3-evidence.md and reports/evidence/lab3/. No screenshot is explicitly required by Lab3, and no capture is claimed. No Lab4 work has begun.

## Lab 4 — technical work VERIFIED; final submission APPROVED

Objective: refine design/patterns/separation and deploy a container. Learning: Repository/Service/DTO patterns; find coupling/design flaws; first-class OpenAPI; Docker build/run.

- A: review all src for controller logic, direct repository coupling, entity API exposure, missing global error handler, and OpenAPI annotations; rate CRITICAL/IMPORTANT/MINOR; student reviews findings.
- B: **BookDTO** (exact seven Book fields), **BookMapper** with toDTO/toEntity; BookController signatures use DTOs, never Book. Apply same DTO/mapper pattern to Member and Borrowing; all controllers must avoid entity responses. Preserve API behavior/tests.
- C: **GlobalExceptionHandler** with @ControllerAdvice: IllegalStateException -> 409; EntityNotFoundException or NoSuchElementException -> 404; MethodArgumentNotValidException -> 400 with field errors; other Exception -> 500. Consistent `error`/`timestamp` body. Integration tests with SpringBootTest/MockMvc prove missing-book 404 and unavailable-book 409. Preserve existing ResponseStatusException 404 behavior when introducing catch-all handling.
- D: all controllers have @Tag, endpoints @Operation and applicable @ApiResponse codes; all DTO fields @Schema/examples. Set Swagger `/swagger-ui.html`, JSON `/api-docs`. Open UI and verify every endpoint.
- E: **Dockerfile** multi-stage `maven:3.9-eclipse-temurin-17` build with `mvn package -DskipTests`, runtime `eclipse-temurin:17-jre-alpine`, port 8080, `-Xmx256m -Xms128m`; **.dockerignore**. Execute `docker build -t library-api:lab4 .`, `docker run -p 8080:8080 library-api:lab4`, `curl http://localhost:8080/books`; record actual startup/response. Missing Docker is an external prerequisite, not a claimed successful build.
- F: commit `lab4: DTOs, exception handler, OpenAPI docs, Dockerfile`, tag lab-4, push.
- **reports/lab4-report.md**: full design review and two most serious issues using design vocabulary; specific DTO/API exposure example; two design patterns/classes/problems; multistage deployment benefits and PostgreSQL changes.
- Rubric: all controllers use DTOs/no entity responses (3); global-handler tests pass (2); Swagger documents all endpoints (2); Docker image builds and starts (3).
- Initial comparison (recorded before changes): entity responses, Book/Member direct repository coupling, local handlers, missing intentional OpenAPI and Docker artifacts. Full severity review is reports/evidence/lab4/design-review.md and pasted into reports/lab4-report.md.
- A/B completed: BookService/MemberService; BookDTO/MemberDTO/BorrowingDTO/ReservationDTO plus four mappers and two validated requestDTOs; all four controllers expose DTOs only. Existing nested JSON/ACs preserved; no POM/dependency/entity/domain-service/old-test changes.
- C completed: GlobalExceptionHandler with409/404/400(fieldErrors)/500, error/timestamp payload, preserved ResponseStatusException404. Seven new integration cases cover required borrowing404/409 and remaining mappings; six design/DTO/OpenAPI/CRUD cases supplement them.
- D completed: four Tags,14 documented operations and seven annotated DTO schemas, configured /swagger-ui.html and /api-docs. Actual running container UI opened in isolated headless Edge; real screenshot/DOM inspected and14 blocks/4 groups counted. Live JSON contains no entity schemas.
- E completed: exact prescribed multistage Dockerfile/.dockerignore, real successful `docker build -t library-api:lab4 .`, `docker run -p 8080:8080 library-api:lab4`; container1e4523711063 (vibrant_lamarr) running on8080. curl.exe returned HTTP200 with5 sample books. Backend was initially stopped and started with approval; no WSL reinstall needed.
- Final host `mvn test`: **81 tests,0 failures,0 errors,0 skipped**; original68 unchanged/passing.20 protected files/POM hash-identical. Evidence: reports/evidence/lab4/ and docs/lab4-evidence.md. All four report questions answered with actual evidence.
- Rubric status: all-controller DTOs(3), handler tests(2), actual documented UI(2), actual image/container(3) technically verified. No self-awarded grade. The student approved the reviewed48-file submission, exact commit, lab-4 tag and push; Git execution/remote verification follow this approved snapshot.
- Screenshots: real overview13-swagger-ui.png saved. Optional student captures: expand response/example panels, Docker Desktop + curl output, final Git after submission. No screenshot blocker remains.

## Lab 5 — technical work/report VERIFIED; final submission APPROVED

Objective: name smells, targeted refactorings, regression evidence. Learning: named smells, Extract Method/Replace Magic Number/other patterns, tests before/after, AI technical debt.

- A: `mvn test` before edits; record baseline count/pass rate.
- B: inspect entire src/main/java; quote source evidence for at least 5 prioritized smells from the supplied list (Long Method >15 lines, Magic Numbers, Feature Envy, God Class, Duplicate Code, Primitive Obsession, Inappropriate Intimacy); select three, at least one in BorrowingService.
- C: Extract Method with before/after diff, run `mvn test`; if failure, revert that refactoring and diagnose before continuing.
- **Specification conflict and explicit adaptation:** Lab2 requires public calculateFine and direct tests; Lab5's sample assumes an inline fine calculation and asks for the same signature privately. Preserve the existing public date-based API; extract a private long-overdueDays overload and a return-state helper. No artificial inline/re-extract, changed tests, or claim of prior professor acceptance. The current full-Lab5 instruction authorizes the compatible internal change; the actual adaptation is presented in the final review before submission approval.
- D: static final **DAILY_FINE_RATE** BigDecimal and **DEFAULT_LOAN_DAYS** int replace 0.50/14; `mvn test` immediately afterward.
- E: one further named smell/refactoring with rationale/diff; `mvn test` immediately afterward. Keep the whole suite green after each separate transformation.
- F: for each of 3 changes, record smell/pattern/specific before-after lines/maintenance-cost explanation.
- G: final `mvn test`; commit `lab5: three refactorings — extract method, magic numbers, [your third]` with actual third pattern, tag lab-5, push.
- **reports/lab5-report.md**: smell catalog/severity/maintainability rationale; 3 before-after logs; personal AI-debt reflection with evidence; regression safety and what each run would catch.
- Rubric: 3 distinct named smells with code evidence (3); 3 refactorings with tests before/after each (4); specific maintenance connection (3).
- Initial comparison: fine method already extracted and public, literals unnamed. An early adaptation question was asked without an answer being claimed. The agent then corrected its unnecessary extra approval gate: compatible reversible implementation is covered by the student's explicit full-Lab5 instruction. Full rationale and source changes are recorded in the report/evidence for final review.
- A completed before source edits: actual `mvn test` baseline **81 tests, 0 failures, 0 errors, 0 skipped, exit 0, BUILD SUCCESS**. Evidence `reports/evidence/lab5/01-baseline.txt` and suite JSON.
- B completed: all29 production Java files inspected and frozen with original line numbers; six confirmed findings across Long Method, Magic Numbers, Duplicate Code. All seven requested categories assessed without inventing findings. Full catalog `reports/evidence/lab5/03-smell-catalog.md`, reproduced in `reports/lab5-report.md`.
- E executed first while Step C decision is pending: **Extract Class** `ErrorResponseFactory` removes six duplicate envelope implementations while preserving handler status/message/field-error policy. Complete regression suite **81/0/0/0**, exit0. Actual snapshots/patch/log/suite JSON use prefix04 in the lab evidence directory.
- D executed second: exact **DAILY_FINE_RATE** BigDecimal and **DEFAULT_LOAN_DAYS** int replace policy literals. Complete regression suite **81/0/0/0**, exit0. Actual snapshots/patch/log/suite JSON use prefix05.
- C executed third: **Extract Method** introduces private `applyReturnState(Borrowing)` and `calculateFine(long)` while preserving public fine API, ordering and transactions. Return orchestration19->11 physical lines (15->9 nonblank); no fine formula was falsely claimed inline. Complete suite **81/0/0/0**, exit0; prefix06 snapshots/patch/log/suite summary.
- F/report/evidence complete: full catalog and three real patches; each smell/pattern/changed lines/maintenance cost explained. Structural measures07: unnamed policy use sites2->0, named definitions0->2, error-envelope implementations6->1, mappings6->6; Java files29->30 and physical lines1214->1241. Debt explanation is evidence-based and does not invent personal hours or a human comparator. All four report questions answered in `reports/lab5-report.md`; commands/decisions in `docs/lab5-evidence.md`.
- Final Step G `mvn test`: **81 tests, 0 failures, 0 errors, 0 skipped**, exit0, BUILD SUCCESS, prefix08 evidence. Five complete suites passed (baseline, after each transformation, final). All11 original test files hash-identical;44/46 initial tracked snapshots unchanged; only the two intended existing production files changed, plus new factory. Lab4 report preserved at its initial hash; HEAD and all three prior tags unchanged. Final verification09 and explicit submission list10 are saved.
- G remains approval-gated: exact planned message `lab5: three refactorings — extract method, magic numbers, extract class`; explicit Lab5 paths only, excluding the separate Lab4 report edit and unrelated DOCX/startup files. No commit/tag/push yet. All prior tests/history/tags preserved so far; no Lab6 work.
- Subsequent student instruction approved final submission of the36 reviewed files, exact message, lab-5, origin push and remote verification. Fresh precommit `mvn test`:81/0/0/0, exit0, BUILD SUCCESS. Lab4 report hash preserved; all11 original tests unchanged. Earlier pending markers describe preparation; actual Git results are verified after execution. See `docs/lab5-evidence.md` approved-submission section.
- Screenshots: optional baseline and each of three green summaries with diffs. Preserve separate raw outputs even if not captured.

## Lab 6 — technical measurements/fix VERIFIED; final submission APPROVED

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
- A completed: exact three requested plugin versions/configuration installed in POM, with JaCoCo report bound to test phase. Existing application dependencies unchanged.
- B completed: actual PMD/SpotBugs/coverage/check goals executed. target/pmd.xml, target/spotbugsXml.xml and target/site/jacoco/index.html generated; final HTML opened in isolated headless Edge. PMD9 priority3 findings; pmd:check exits0 because failOnViolation=false, not because findings are absent. Essential XML retained in reports/evidence/lab6 (five files only).
- Coverage baseline before production fix:287/303 lines=94.72%; final290/306=94.77%, exceeding40% without coverage-driven production changes. Lowest class LibraryApiApplication1/3=33.33% because static main is not called by context tests. Full class table in report.
- C completed counts: borrowBook1 decision/CC2; returnBook1/2; public calculateFine0/1; private overload1/2; constructor0/1, applyReturnState0/1, active-member query1/2. No methodCC>5. Student supplied official Chapter6 lecture page60 thresholds:1–10 Simple program, no real risk;11–20 Moderately complex and risky program;21–50 Complex and highly risky program;>50 Untestable and extremely risky program. All existing CC1/CC2 values receive the exact first label; calculations unchanged.
- D complete: production explicit-import scope includes framework/JDK/annotation/interface types and excludes tests. BookCe7/Ca1/I0.8750; Borrowing14/1/0.9333 (highest); Member7/1/0.8750; Reservation12/1/0.9231. Complete method and audit in report/evidence.
- E complete: docs/gqm.md has exact Goal/Questions/Metrics/Results/Interpretation template, two questions/metrics with actual values and suggested action; full plan pasted into report.
- F complete: genuine SpotBugs EI_EXPOSE_REP2, ErrorResponseDTO constructor, original line9, priority2/rank18; engineering judgment IMPORTANT rather than fabricated tool-critical severity. Defensive immutable map snapshot preserves HTTP contract/nulls/order. Same rule/class/method1->0; total SpotBugs11->10; accessor warning remains explicitly recorded, no suppressions. New3-case regression suite validates input/output aliasing and null behavior.
- Final standalone mvn test:84 passed,0 failures/errors/skips; original81 cases and all11 original test files preserved. Source changes limited to DTO fix plus POM/new tests; Lab4 report hash preserved; history/tags unchanged; no Lab7 work.
- Five-question report and compact evidence complete: reports/lab6-report.md and docs/lab6-evidence.md. Student confirms almost all Java was Codex-generated; student provided requirements, reviewed/approved changes and verified functionality through tests. Report uses actual coverage/coupling/CC values and explains why no verified manual-code cohort exists for an AI-vs-manual comparison. Official page60 risk bands are applied; no outstanding threshold/authorship clarification or fabricated manual metrics remains.
- G prepared only: final tests complete; exact message `lab6: metrics — PMD, SpotBugs, JaCoCo, cyclomatic complexity analysis`; no staging/commit/lab-6/push before approval. Exclude pending Lab4 report, original DOCX and startup-error.txt. Existing lab-1/lab-3/lab-4/lab-5 remain preserved; missing lab-2 remains noted.
- Subsequent student instruction approved the final12-file Lab6 submission, exact message, lab-6 and origin push/remote verification. Fresh precommit suite84/0/0/0, exit0; reviewed evidence and pending Lab4 report preserved. Earlier pending markers describe preparation. Actual resulting Git refs are verified after execution; no Lab7 work is included.
- Screenshots: JaCoCo summary (table alternative permitted), static-analysis before/after. Continue with actual XML/table evidence if screenshots pending.

## Lab 7 — technical work/report VERIFIED; final submission APPROVED

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
- A completed: baseline84 tests,0 failures/errors/skips; overall lines290/306=94.77%, branches30/38=78.95%; service lines100/102=98.04%, branches20/20=100%. Exact baseline XML retained; no coverage exclusions/config changes.
- B completed: actual initial14-method BorrowingServiceUnitTest covers prescribed12 scenarios plus public active-member query. MockitoExtension/@Mock repositories, no Spring;14/0/0/0. All previous tests preserved.
- C completed AI source audit: initial14 Meaningful,0 vacuous Weak/0 Wrong oracles;3 Missing scenarios genuinely identified and fixed (repeat return/no writes, empty active list, cross-month fine), plus strengthened negative-operation/no-repository-interaction assertions. Audited17-unit suite passed. All22 generated methods including3 real-HTTP/2 catalog cases classified with reasons in report; initial source retained. Student must review this actual audit before submission approval; no prior human manual review is fabricated.
- D completed: exact3 RANDOM_PORT/TestRestTemplate flows, with real HTTP persistence/state/error oracles. Two catalog MockMvc tests cover previously missed list queries and returned record fields. Full pre-experiment suite106/0/0/0.
- E completed: actual rate1.00 mutant causes8 failures/98 passes; exact source restored and full106-case suite green. Independent null-zero mutant causes14 failures/92 passes; restored bytes and final106-case suite green. Both mutants have0 errors/skips. Full failing logs and per-case pass/fail JSON retained; no defect remains or is committed.
- F/final complete: fresh JaCoCo execution data, final106 tests all passing. Overall lines296/306=96.73%, service102/102=100%, controller36/36=100%; overall branches30/38=78.95%, service20/20=100%. Remaining gaps are mapper-null guards and static main; no artificial tests merely inflate coverage. All original source/POM and12 prior test files match saved hashes; original84 cases all pass. Prior tags/history and pending Lab4 report preserved.
- Report/evidence complete: one five-question reports/lab7-report.md and docs/lab7-evidence.md with strategy, coverage before/after,22-method audit, actual improvements, exact bug detections/non-detections, evals/AC link and two concrete human-review examples. Six essential raw artifacts only. Exact12-path submission list prepared in evidence and target; exclude Lab4 report/DOCX/startup inputs.
- G approval-gated: exact message `lab7: test suite, MockMvc integration tests, 80%+ coverage`; no staging, commit, lab-7 or push yet. Lab8 is not started. Lab2's missing tag remains noted.
- Subsequent student instruction approved the final12-file submission, exact message, lab-7 and origin push/verification. Fresh precommit suite106/0/0/0, overall line coverage96.73%, services100%; both deliberate defects fully restored and all original source/test/config hashes preserved. Earlier pending markers describe preparation; Git outcomes are verified after execution. Lab8 remains unstarted.
- Screenshots: before/after JaCoCo (tables permitted), both real failing experiment summaries, final restored GREEN. Keep raw logs regardless of captures.

## Lab 8 — ACTIVE; independent calculations complete, personal effort inputs pending

Objective: function points and COCOMO compared with actual effort. Learning: UFP counts, organic COCOMO81, all-lab effort comparison, AI LOC critique.

- A: inventory all external inputs/outputs/inquiries, internal logical files, external interfaces; classify simple/average/complex and justify using fields/rules. Student makes final classification. Fill supplied UFP weights EI 3/4/6, EO 4/5/7, EQ 3/4/6, ILF 7/10/15, EIF 5/7/10; sum actual weighted counts.
- B: estimated days **UFP/9**; hours **days*7.5** from stated 8–10 FP/day midpoint.
- C: all 8 labs' original estimated and actual hours, totals, ratios; honest retrospective estimates explicitly labeled if not tracked. Calculate EQF with the **DeMarco formula from the lecture**; obtain that exact formula/notes if not provided, rather than assume an alternative formula.
- D: hypothetical Fine Payment analysis (not production feature): GET /members/{id}/fines, POST /fines/{id}/pay, GET /reports/fines?month=YYYY-MM; reasoned min/best/max LOC estimates labeled estimates. Weighted size **(min+4*best+max)/6**; KLOC; **HM=2.4*KLOC^1.05**, **TDEV=2.5*HM^0.38**, **team=HM/TDEV**.
- E: execute supplied `find src/main/java -name "*.java" | xargs wc -l | tail -1` and `find src/main/java -name "*.java" | wc -l`, or document equivalent PowerShell counting semantics. Compute real LOC/hour and FP/hour with actual total hours.
- F: commit `lab8: FP estimation, COCOMO analysis, effort retrospective`, tag lab-8, push.
- **reports/lab8-report.md**: full UFP table/complexity rationale; all-lab effort table/ratios and velocity multiplier; numerical LOC vs FP critique; modified alpha supported by actual productivity calculation; student's personal lifecycle reflection. Estimates must never masquerade as measured quantities.
- Rubric: justified UFP table (3); estimated/actual hours all8 (3); correct numeric COCOMO (2); AI/lifecycle final reflection (2).
- Assignment and supplied46-page Chapter8 lecture read: pages23–29 FP definitions/weights,15–16 COCOMO/152-hour person-month,31–32 exact area-based DeMarco EQF. No ratio is falsely labeled EQF. Fixed-estimate approximation is explicitly distinguished from a dated forecast history.
- A/B proposed count complete for student judgment:8 EI33FP,0 EO,6 EQ20FP,4 ILF34FP,2 course-convention interfaces12FP =99UFP;99/9=11days;×7.5=82.5hours. All14 endpoints counted once; H2 connector convention and stricter94FP boundary sensitivity disclosed rather than silently double-counting tables. Full fields/rules/complexity reasons in report.
- C recovery/update: only personal original estimate Lab2=10h was recovered; other original forecasts are not recorded and professor durations are not substituted. Lab1 actual approximately2h from memory. Student clarified Lab2 total actual approximately3h from memory, not measured; historical5.5h story sum is not used as the total. Lab2 report/backlog and Lab8 table consistently show approximate ratio0.30. Labs3–7 remain untracked/unknown; Lab8 in progress, actual to be recorded after completion. No project totals/ratios, numerical velocity, EQF, productivity rates or modified numerical alpha are calculated from incomplete inputs.
- D complete hypothetical scope/LOC rationale:260/420/680 ->weighted436.6667LOC,0.4366667KLOC; HM1.005469, TDEV2.505187months, average0.401355FTE,152.8313model hours at152h/month. No extension implemented. AI-alpha scaling2.4×A/E is a stated assumption awaiting real totals.
- E measured:30 production Java files,1249 physical LF-counted lines; LOC/hour1249/A, FP/hour99/A await total actual effort. No invented personal productivity result or manual-code control group.
- Five-question reports/lab8-report.md and compact docs/lab8-evidence.md created; factual lifecycle reflection grounded in recorded requirements/review/testing. Numerical project-effort portions remain visibly incomplete. Intended5-file scope: Lab8 report/evidence/checklist/reconciled backlog plus expressly requested Lab2 report correction; no redundant raw bundles.
- Final main verification:106 tests pass,0 failures/errors/skips; overall lines296/306=96.73%, services102/102=100%. Production/tests/POM and existing tags/history preserved; pending Lab4 report remains excluded. Earlier Docker/Swagger evidence retained; no current live deployment result invented.
- Submission remains approval-gated: exact message `lab8: FP estimation, COCOMO analysis, effort retrospective`; lab-8 not created. Final-course missing lab-2 tag and pending Lab4 report remain visible. Lab8 is not declared finished while effort data is incomplete.
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
