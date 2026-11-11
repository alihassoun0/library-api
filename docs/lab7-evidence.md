# Lab 7 — Software testing evidence

## Authority, assignment and preserved state

Read the complete Lab7 section of the professor's original DOCX with .NET ZIP/XML, including every paragraph/table, Steps A–G, five deliverable questions and2/3/3/2-point rubric. The current user expands this into an explicit strategy and final verification step, and additionally requires ≥80% overall coverage. The professor specifically requires ≥80% **service-package** line coverage. Both targets are measured. The step headed “integration tests with MockMvc” actually specifies RANDOM_PORT + TestRestTemplate for three flows; that exact implementation is used alongside existing/new MockMvc tests.

Initial HEAD/main and origin/main: `ce03a35751cbaae64a7a7c28ebe9a886c355c53b` (lab-6). Tags lab-1, lab-3, lab-4, lab-5, lab-6; no lab-2. Only preexisting changes: pending Lab4 report, untracked assignment DOCX and startup-error.txt. Those are excluded. `docs/AGENTS.md` remains the historical Lab3 specification. No Lab8 work or commit/tag/push is authorized before final review/approval.

Initial source/test/POM/Lab4 report SHA256 and tag refs are saved locally under `target/lab7-before.json` and `target/lab7-tags-before.txt`. Twelve existing test files containing84 cases must remain byte-identical. BorrowingService is modified only temporarily during the two authorized experiments; each experiment restores the exact original bytes in a finally block.

## Execution checklist

- [x] Read full assignment, inspect tests and JaCoCo0.8.11 configuration; record baseline.
- [x] Define unit, persistence/application integration, MockMvc and real-HTTP strategy in the report.
- [x] Generate required comprehensive no-Spring Mockito suite for all public BorrowingService methods; run14 initial cases green.
- [x] Inspect every generated method; identify3 genuinely missing scenarios and incomplete negative-side-effect assertions; improve suite and run17 unit cases green.
- [x] Add3 prescribed RANDOM_PORT/TestRestTemplate flows and2 meaningful catalog MockMvc cases; run106-case full suite green before experiments.
- [x] Change only fine rate0.50->1.00; full suite detects defect; restore original source and run full suite green.
- [x] Change only zero return->null; full suite detects defect; restore original source and run full suite green with final coverage.
- [x] Confirm all prior tests/production/POM/tags/history preserved; finish five-question report, coverage/experiment evidence and exact submission list.
- [ ] Student reviews generated tests/audit and approves final submission; only then exact commit, new lab-7 tag and origin push/verification.

## Actual commands and results

Logs are captured through `cmd /c` redirection, with Maven's immediate `$LASTEXITCODE` recorded. Expected deliberate-bug failures are retained rather than hidden. Timestamps are2026-11-11 local+02:00.

| Command/state | Actual result | Working log |
|---|---|---|
| `mvn test jacoco:report`, original source/tests | Exit0;84/0/0/0; baseline captured at12:38:10 | `target/lab7-baseline.log` |
| `mvn -Dtest=BorrowingServiceUnitTest test`, initial generated suite | Exit0;14/0/0/0;12:39:48 | `target/lab7-generated-tests.log` |
| Same targeted command after source audit/improvements | Exit0;17/0/0/0;12:40:27 | `target/lab7-audited-unit-tests.log` |
| `mvn test jacoco:report`, before experiments | Exit0;106/0/0/0;12:41:48 | `target/lab7-pre-experiments.log` |
| `mvn test`, rate1.00 only | Exit1;106 cases:98 passed,8 failures,0 errors/skips | `target/lab7-bug1-rate.log` |
| `mvn test`, exact source restored after rate experiment | Exit0;106/0/0/0;12:43:44 | `target/lab7-restored-after-bug1.log` |
| `mvn test`, nonpositive fine null only | Exit1;106 cases:92 passed,14 failures,0 errors/skips; source restored in finally | `target/lab7-bug2-null.log` |
| `mvn -Djacoco.append=false test jacoco:report`, correct source restored after null experiment | Exit0;106/0/0/0;12:45:48; fresh final coverage data | `target/lab7-final-tests-coverage.log` |

Four-number summaries above mean tests/failures/errors/skipped. No production configuration or class exclusion is changed. JaCoCo report is already bound to test, so the explicit report goal additionally generates HTML/XML without rerunning tests.

## AI generation, inspection and three concrete improvements

Generation brief followed the professor's exact scope: comprehensive BorrowingService tests, MockitoExtension, @Mock BookRepository/MemberRepository/BorrowingRepository, no Spring; borrow success/missing book/missing member/unavailable; return on-time/three days late/missing borrowing/exact due; fines0/1/7/negative days; include active-member query because every public method must be covered. Saved the actual initial14-method source before audit, not an intentionally broken test suite.

Review brief: classify each method Meaningful/Weak/Wrong oracle; identify2–3 wholly missing scenarios; examine assertions, independent oracles, boundaries, isolation, mutation sensitivity and vacuity. Codex performed source-level review; the complete table is presented to the student for the professor's human-review step. No student manual review is fabricated. Submission approval must follow review of this concrete output.

The14 initial methods were Meaningful, with0 vacuous Weak tests and0 Wrong oracles. This does not mean they were complete. Missing scenarios were: repeated return with state preserved/no writes; existing member with no active loans; elapsed fine days across a calendar-month boundary. Added those3 cases. Also strengthened four exception tests with never-save/no-interaction assertions; missing-member rejection now proves availability remains true. Four pure-fine cases now assert no repository interactions. These are actual improvements to incomplete oracles; tests were not deliberately weakened merely to manufacture audit findings. The original snapshot and final source permit direct comparison. Initial fine0/1/7 cases overlap existing fine units because the professor explicitly requires them in this generated suite; existing tests are preserved rather than deleted as “redundant.”

Isolation: Mockito initializes fresh mocks per method, fresh entities in BeforeEach, no lenient/global stubs, return stubs local to relevant cases. Fine arithmetic uses fixed dates and independent expected decimal strings; tests do not read production constants. Return workflows use LocalDate.now because the unchanged production API owns its clock; a midnight crossover remains a known timing limitation, not a reason to change business code here. Real HTTP tests create unique book/member records per flow, assert only their own generated IDs, and do not assume rollback across server threads. Catalog MockMvc tests use transactional fixtures and locate the exact saved record in the response, without depending on global list counts or ordering.

## Deliberate bugs and restoration

Correct policy is DAILY_FINE_RATE BigDecimal0.50, with nonpositive overdue days returning the non-null BigDecimal.ZERO singleton. The formula is already in the private fine overload introduced in Lab5, not inline in returnBook.

Experiment1 changes only the constant's value to1.00 (the modern location of the professor's specified fine-rate defect). Source is restored byte-for-byte in finally. An initial verifier mistakenly compared a scalar hash with the entire parsed JSON array due to Windows PowerShell pipeline enumeration; explicit snapshot selection confirmed the restored SHA256 exactly matches the original. This was a verifier issue, not a failed restoration or an invented test failure. The full post-restore suite then passed106 cases.

Each experiment records every Surefire testcase identity/outcome, including all non-detections. The report lists exact failing cases and per-suite passing counts; the compact experiment JSON contains the exhaustive identities, failure/error distinction and assertion messages. Rate-sensitive cases should fail only on positive overdue amounts; zero-fine and unrelated catalog/reservation tests are not expected to detect that rate mutation. The second mutation independently tests null-versus-zero oracles.

Experiment2 changes only `return BigDecimal.ZERO;` to `return null;` in the private long-overdueDays helper; the0.50 rate remains correct. It causes14 assertion failures and92 passes, not runtime errors. In addition to both fine suites and new zero-boundary tests, the persistence, original return controller/service and real-HTTP return flow detect it. Positive-overdue tests remain green because their amounts are unchanged. After restoration, all106 cases pass; expected failing runs are not presented as green builds. Exact original BorrowingService SHA256 after both restorations: `D4E26AE9D8C687FCA5E6F314916A33F957C2A2D97474CF88D40115A93FA0D4E5`.

## Final coverage, preservation and report checks

Read direct root/package LINE and BRANCH counters from baseline/final XML. Overall lines **290/306=94.77% ->296/306=96.73%**; overall branches **30/38=78.95% ->30/38=78.95%**. Service lines **100/102=98.04% ->102/102=100%**; service branches **20/20=100%** in both states. Controllers **32/36=88.89% ->36/36=100%**. Mapper line coverage remains54/62=87.10% and branches8/16=50%;8 missed mapper null returns and2 main-entry lines explain the final10 missed lines. No service class was below80% before this lab; uncovered list queries were nevertheless tested meaningfully. Final agent arguments explicitly show append=false, so coverage uses a fresh full-suite run with no mutation execution data appended.

The report contains strategy, before/after package and relevant-class tables, all22 generated method classifications, initial/final audit counts, actual issue corrections, exact8/14 failing-case identities, per-suite non-detections and the Lab3-AC/evals connection. Initial14 cases and audited17 cases each ran green; real HTTP and catalog cases bring the full total from84 to106. No wrong oracle or vacuous Weak test was fabricated. The student's human review is explicitly pending at the approval boundary, while Codex's actual source audit is documented accurately.

All initial source/test/POM/Lab4-report SHA256 entries match after final restoration, including all12 original test files. The final Surefire inventory confirms all84 original cases and22 new cases passed. No existing production, POM or test file differs from Lab6; only3 new test classes are submitted. Existing lab1–6 reports/evidence and previous tags/history remain unchanged, except the authorized checklist update. No lab-7 tag or commit exists yet; lab-2 remains absent. No Lab8 work occurred. Complete per-case mutation/final results and preservation facts are in retained `experiments.json`.

Exact submission scope (12 files) is listed in the final review and local `target/lab7-submission-files.txt`:3 new test classes, report, this evidence document, checklist, and6 raw artifacts. Exclude the pending Lab4 report edit, assignment DOCX and startup-error.txt. Exact professor commit message is `lab7: test suite, MockMvc integration tests, 80%+ coverage`; commit, lab-7 tag and push require student approval after reviewing the concrete tests/audit. No submission outcome is claimed in advance.

```text
docs/reference-labs-checklist.md
docs/lab7-evidence.md
reports/lab7-report.md
src/test/java/com/example/libraryapi/service/BorrowingServiceUnitTest.java
src/test/java/com/example/libraryapi/controller/BorrowingHttpIntegrationTest.java
src/test/java/com/example/libraryapi/controller/CatalogListControllerTest.java
reports/evidence/lab7/jacoco-before.xml
reports/evidence/lab7/jacoco-final.xml
reports/evidence/lab7/generated-suite-before-audit.java.txt
reports/evidence/lab7/bug1-rate.log
reports/evidence/lab7/bug2-null.log
reports/evidence/lab7/experiments.json
```

## Compact retained evidence and screenshot opportunities

Use one report and this one evidence document. Essential raw artifacts only: baseline/final JaCoCo XML, original generated-suite snapshot, two deliberate-bug Maven logs, and one experiment JSON containing exact pass/fail identities. Other working logs and preservation/measurement JSON remain in ignored target. No separate per-step prose files or screenshot bundles are created.

Optional screenshots: baseline/final JaCoCo line and branch summary, initial/audited suite diff, each real failing bug summary, restored green summary, final Git refs after approved submission. Screenshots do not block technical work. No captured screenshot is claimed.

## Approved final submission

The student explicitly approved the final Lab7 submission after the tests/audit/report were presented, including inspection of all12 paths, restoration of both deliberate bugs, fresh tests/coverage, exact commit message, lab-7, origin push and remote verification. This supersedes earlier pending-review/approval markers, which record the preparation stage; no separate unrecorded human audit is claimed.

Reinspected all12 files:3 test classes, report/evidence/checklist and6 retained raw artifacts. Parsed XML/experiment JSON, checked raw8/14-failure logs and initial14-method source, and verified every original source/test/POM/Lab4-report hash. Correct rate0.50 and nonpositive-day BigDecimal.ZERO are restored byte-for-byte. Fresh `mvn -Djacoco.append=false test jacoco:report` finished2026-11-11 12:52:21 +02:00: **106 passed,0 failures/errors/skips, exit0, BUILD SUCCESS**; overall296/306=96.73%, service102/102=100%. Working log `target/lab7-submission-tests.log`. Fetch found no newer remote main commits. Only approval-status documentation changed; original code/tests/config and retained evidence remain as reviewed. Stage exactly the12 listed files and inspect the index before executing the approved commit/tag/push. Resulting refs are verified afterward and saved separately in ignored `target/lab7-submission-verification.json`; future Git outcomes are not claimed in this precommit snapshot. Lab8 remains unstarted.
