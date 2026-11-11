# Lab 5 — Refactoring evidence

## Authority and boundaries

The student explicitly authorized completing Lab 5 Steps A–G, the four report questions, evidence/checklist updates, and preservation of Lab 1–4 behavior/tests/history/tags. Commit, tag, and push require approval after final review. Lab 6 is not authorized. `docs/AGENTS.md` remains the unchanged historical Lab 3 reservation specification; the explicit Lab 5 instruction authorizes the selected refactorings without rewriting it.

Read the complete original DOCX Lab 5 section through .NET ZIP/XML APIs without modifying it. Included nested table paragraphs to read the grading rubric. Exact extracted text: `reports/evidence/lab5/00-assignment-lab5.txt`.

Initial state: HEAD/main and locally recorded origin/main `5c8ac59`; existing tags `lab-1`, `lab-3`, `lab-4`, no `lab-2` or `lab-5`. Existing modified `reports/lab4-report.md` is unrelated and preserved. Untracked `SE_Lab_Assignment_2026_2027_Codex.docx` and `startup-error.txt` remain excluded.

## Commands and actual execution order

1. `git status --short`; `git log --oneline --decorate -12`; `git tag --list`; `rg --files --hidden -g AGENTS.md -g '!target/**' -g '!.git/**'`; read checklist, POM, historical specification, and complete assignment.
2. Created `reports/evidence/lab5` and captured SHA256 of tracked source/tests/POM/Docker files and the current Lab 4 report in `00-before-manifest.json`. Saved original tag refs in `00-tags-before.txt`.
3. Before any source edit: `mvn test`, captured with `cmd /c "mvn test > reports\evidence\lab5\01-baseline.txt 2>&1"`; saved and checked `$LASTEXITCODE` before displaying the log tail. Exit **0**, **81 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**. Parsed all Surefire suite XML into `01-baseline-results.json`. The command finished 2026-11-11 11:45:19 +02:00.
4. Read every Java source file (29 files) with per-file numbering. Saved the full original listing to `02-source-before.txt`. Read relevant fine, service, persistence, handler, DTO/OpenAPI and reservation tests. Full Step B output: `03-smell-catalog.md`.
5. Identified Step C's incorrect sample premise: the public date-based fine method already exists and is directly tested. Asked the student whether to preserve it and extract a private `calculateFine(long overdueDays)` overload. Independent E/D work proceeded while the question was pending. No student answer to that early question is claimed; see the subsequent implementation decision in item 8. No artificial inline/re-extract or test weakening was performed.
6. **Step E executed first:** Extract Class `ErrorResponseFactory` from six repeated error-envelope construction sites. Saved original/after handler snapshots and actual Git patch (including the new class) in `04-extract-class-before.java.txt`, `04-extract-class-after.java.txt`, `04-extract-class.patch`. No business rules, exception annotations, messages, field errors or status choices were changed. Ran the complete `mvn test` before making another transformation: **exit 0, 81/0/0/0, BUILD SUCCESS**. Log `04-extract-class-tests.txt`; suite summary `04-extract-class-results.json`; finished 2026-11-11 11:47:57 +02:00.
7. **Step D executed second:** added `DAILY_FINE_RATE` as BigDecimal `"0.50"` and `DEFAULT_LOAN_DAYS` as int `14`; changed only the two policy references in BorrowingService. Original/after snapshots and a real `git diff --no-index` patch: `05-constants-before.java.txt`, `05-constants-after.java.txt`, `05-constants.patch`. Complete `mvn test`: **exit 0, 81/0/0/0, BUILD SUCCESS**. Log `05-constants-tests.txt`; suite summary `05-constants-results.json`; finished 2026-11-11 11:48:45 +02:00.
8. A server restart interrupted the turn. On resumption, rechecked Git status/history/tags, source and saved evidence before repeating any action. Verified BorrowingService's SHA256 exactly matched `05-constants-after.java.txt`; no additional source change had occurred since the passing Step D suite. The agent corrected its earlier unnecessary approval gate: the student's current explicit instruction to finish Lab 5 already authorized a compatible reversible implementation that preserves the Lab 2 API. This is an implementation judgment, not a fabricated student/professor approval of the alternative signature.
9. **Step C executed third:** Extract Method moves return-state/fine/availability assignments into private `applyReturnState(Borrowing)` and day-to-amount calculation into private `calculateFine(long overdueDays)`. Public `calculateFine(LocalDate, LocalDate)` and its callers/tests remain intact. The before/after patch is `06-extract-method.patch`, with complete source snapshots beside it. Original call/statement order, transactions, error messages, zero identity, decimal scale/rate and persistence calls are retained. Complete `mvn test`: **exit 0, 81/0/0/0, BUILD SUCCESS**; finished 2026-11-11 11:54:39 +02:00. Log `06-extract-method-tests.txt`, suite summary `06-extract-method-results.json`.
10. Measured the real before/after source: return method 19 physical/15 nonblank lines ->11/9; new return-state helper10 physical lines; public fine wrapper7->4 plus private6; unnamed policy use sites2->0; named definitions0->2; envelope implementations6->1; exception mappings6->6; production files29->30 and physical lines1214->1241. Saved counting logic `07-measure-structure.ps1` and results `07-structural-measures.json`. No complexity, coverage, effort or monetary-debt score is implied.
11. **Step G final regression check:** complete `mvn test` again, with unchanged final production source, **exit 0, 81 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**; finished 2026-11-11 11:57:15 +02:00. Raw log `08-final-tests.txt`, suite summary `08-final-results.json`. Five separate complete suite runs were executed in total: baseline, after each of three transformations, final.
12. SHA256 preservation check against46 initial tracked snapshots: **44 unchanged**; only the two intentionally edited existing Java files differ. All11 original test files are byte-identical, with original suite names/counts retained. POM, Dockerfile, .dockerignore, resources, controllers, DTOs, entities, repositories, mappers and remaining services are unchanged. The one new production file is ErrorResponseFactory. The pending Lab4 report retains exactly its Lab5-start hash. HEAD remains5c8ac59, all three original tag refs match, no lab-5 exists. Full data: `09-final-verification.json`.
13. Completed the four-question report with the full Step B output, all three actual patches, structural measurements, debt assessment and regression oracles. Updated the Lab5 checklist. The36 explicit intended submission paths are saved in `10-submission-files.txt`; the separate Lab4 report and unrelated assignment/startup files are excluded. Verified that the report contains the complete catalog and all three complete actual patches. The list is for review; nothing is staged, and no commit/tag/push is claimed.

The prior full-suite run is the before-check for each next transformation, with no intervening production edits. Raw Maven logs are separate for each state. Exit 1 from `git diff --no-index` indicates that the captured files differ; it is not a test failure. PowerShell/native Git may print LF/CRLF conversion warnings; these are not Maven failures.

## Findings and decisions

- Six confirmed findings: policy magic numbers, duplicated error envelopes, three long workflow methods, duplicated member-existence guard. Three distinct smell categories satisfy the catalog rubric. Potential value objects are documented as candidates, not counted as invented smells. Other requested categories were assessed without forcing positive findings.
- Selected categories: Long Method (return/fine workflow), Magic Numbers (loan policy), Duplicate Code (error envelope). Keep all original tests/assertions; no dependencies, entity mappings, HTTP contracts or migrations are changed.
- Small static package-private response factory keeps construction separate from exception-to-status selection without introducing another Spring dependency. It uses `HttpStatusCode` to preserve arbitrary `ResponseStatusException` codes.
- Fine extraction uses an explicit adaptation because the professor's private date-signature example conflicts with the existing public Lab2 API. Keeping a public and private method with the same date signature in one Java class is impossible. The original seven-line fine method is not falsely called long. Long Method evidence refers to the 19-line `returnBook` orchestration, now11 lines. The student's final review includes this deviation; prior professor acceptance is not claimed.
- No manual experiments, performance gains, developer-hour savings, grades, or coverage figures are invented.

## Technical debt and regression evidence

Report section 2 embeds actual per-transformation patches; section 3 explains debt and limits; section 4 names the existing regression oracles and what a behavior change would break. The catalog is reproduced completely in section 1, including its historical proposed-decision note; section 2 explains the actual final decision. Measurements, final preservation hashes and the final regression suite are saved in the07/08/09 artifacts.

### Tooling issues and corrections

- Direct execution of the saved measurement `.ps1` was blocked by the host's default script execution policy. Ran the equivalent read-only measurement commands interactively; did not change execution policy or system settings. The saved script documents the counting algorithm.
- Initial final-verification parsing wrapped the JSON array from Windows PowerShell `ConvertFrom-Json` in another array, so it compared a collection rather than individual snapshots and rejected the result. Removed the extra wrapper and used explicit snapshot iteration. The corrected comparison found only the two intended modified files. This was a verifier error, not a source/test failure; no failing application test is invented, and no source was altered to conceal it.
- Git prints expected line-ending conversion warnings. Intentional Markdown line breaks and raw diff/log formatting are preserved; source/checklist whitespace checks pass.

## Submission and screenshots

- [x] Original assignment/rubric read and saved.
- [x] Full source catalog and raw baseline/first two regression runs saved.
- [x] Step C adaptation implemented and documented for review; full suite afterward.
- [x] All three actual patches, structural measurements, and maintenance rationale saved.
- [x] Final tests, preservation verification, and four-question report/checklist completed.
- [x] Student approval of explicit Lab 5 submission file list.
- [ ] Exact approved commit; new lab-5 tag; push and remote verification.

Screenshots are optional for Lab 5; the assignment requires diffs and test evidence, supplied as raw text and report tables. No screenshot capture is claimed. Optional capture checklist: original baseline summary, each transformation's patch plus passing suite, final Git refs after approved submission. There is no screenshot blocker.

## Approved final submission

The student explicitly approved the Lab 5 final submission and requested inspection of all36 files, exclusion of unrelated files, preservation of the pending Lab4 report, a fresh81-case test check, the exact commit message, lab-5 on that commit, push to the existing origin, and remote verification. This supersedes the earlier pending-approval markers, which describe the preparation stage.

Reinspected all36 listed files, source diffs, all five original suite summaries, source snapshots, JSON evidence, report/catalog/patch inclusion, initial Git refs and origin. Checked all46 prior snapshot hashes against final verification; the pending Lab4 report is preserved byte-for-byte and excluded. Fresh `mvn test` before submission: **81 tests, 0 failures, 0 errors, 0 skipped, exit0, BUILD SUCCESS**, finished 2026-11-11 12:03:56 +02:00. Raw output is saved separately in ignored `target/lab5-precommit-tests.txt`, preserving the reviewed36-file submission list. No source or tests were changed during final submission. Stage only those36 paths and inspect the index before the approved commit. Resulting commit/tag/remote refs will be verified after execution and saved under ignored `target/lab5-submission-verification.json`; their future outcomes are not asserted here.
