# Lab 2 — Execution evidence

Authoritative source: `SE_Lab_Assignment_2026_2027_Codex.docx`, version 1.0, July 2026; full document inspected through read-only ZIP/XML extraction. This log records actual activity, not personal effort hours.

## Initial inspection

Commands executed before changes: `git status --short`, `git log --oneline -8`, `git tag --list`, `rg --files --hidden -g '!.git/**' -g '!target/**'`, `git remote -v`, `git diff --stat`, `git ls-files reports docs`, `git show --no-patch --format=fuller 3fedd09`, `git rev-parse lab-1`. PowerShell `Get-Content` inspected the service, controllers, entities, tests, POM, backlog, and existing Surefire reports. `System.IO.Compression.ZipFile.OpenRead` read `word/document.xml`; XML paragraphs were printed with indices, then paragraphs 199–365 were reread separately to recover the output truncated during the full-document read. No assignment document edits were made.

Observed state: branch `main`; remote `origin` is the existing `https://github.com/alihassoun0/library-api.git`; return endpoint implementation present but uncommitted; only `lab-1` tag present. Assignment DOCX and `startup-error.txt` are untracked input files and are excluded from staging. No credential files were read or committed.

## Prior session facts

- `452aaaf51e2aa233a3edfd047e6d9a5dfa176b03`: original Lab 1 implementation; `lab-1` points here and will be preserved.
- `37ad1b1`: Lab 1 report commit, also preserved.
- `e629c25`: truthful checkpoint of existing Lab 2 implementation, original tests, POM dependency, and backlog. It is not an original test-first commit.
- `3fedd0978510d8f266884a07bf77f9f36b25bb96`: return-endpoint RED test. Its commit message records `mvn test`: 9 cases, 1 failure, 0 errors, 0 skipped; HTTP 200 expected, HTTP 404 received. Eight pre-existing cases passed.
- Subsequent approved return-endpoint implementation: `PUT /borrowings/{id}/return` delegates to existing `returnBook()` and returns HTTP 200 JSON. The earlier full suite passed 9 cases. No old logs or screenshots are reconstructed or invented.

## Current command log

1. `New-Item -ItemType Directory -Force reports/evidence/lab2 | Out-Null` — created the evidence directory.
2. `mvn test 2>&1 | Tee-Object -FilePath reports/evidence/lab2/01-baseline.txt; exit $LASTEXITCODE` — exit 0, **9 tests, 0 failures, 0 errors, 0 skipped**. Raw output is retained in that file. Current run confirms the already implemented return endpoint's GREEN state.
3. Converted the baseline log from Windows PowerShell's UTF-16 default to UTF-8 using `Get-Content -Raw` and `System.IO.File.WriteAllText` with `UTF8Encoding(false)`. Text content was preserved. `git diff --cached --check` reported Maven's own trailing spaces in the raw log; source checks are kept separate rather than altering execution output.
4. `git add -- src/main/java/com/example/libraryapi/controller/BorrowingController.java docs/lab2-evidence.md docs/lab2-xp-review.md reports/evidence/lab2/01-baseline.txt` and encoding-only restaging of that log. Inspected `git diff --cached --stat`, `git diff --cached --check`, and `git diff --cached --name-only` before commit.
5. `git commit -m "lab2: GREEN return endpoint and record XP review"` with an evidence body — created **b1a382a**. No history rewrite, tag, or push performed at this point.
6. Added tests only: `BorrowingFineUnitTest` (5 fixed-date cases), `BorrowingErrorControllerTest` (6 HTTP cases), and `BorrowingPersistenceTest` (1 flush/clear regression). Existing tests/assertions and production code were untouched.
7. `mvn test 2>&1 | Tee-Object -FilePath reports/evidence/lab2/02-review-red.txt` — captured Maven exit code before encoding-only UTF-8 normalization and exited with it. **Exit 1: 21 tests, 0 assertion failures, 2 errors, 0 skipped.** Both errors are unhandled `IllegalStateException` from the unavailable-book and repeated-return HTTP requests. All 19 other cases pass, including the original 9, direct fine tests, missing-record checks, and database-reload test. This is a genuine test-first RED for R1; R2 and R4 add protection to existing correct behavior and are not falsely claimed as newly observed failures.
8. Staged only the three new test classes, RED log, and evidence update using explicit `git add --` paths. Inspected `git diff --cached --stat`, `--name-only`, and `git diff --cached --check -- . ':(exclude)reports/evidence/**'` (source/doc checks passed). `git commit -m "lab2: RED conflict tests and direct fine unit coverage"` with actual-results body created **078fa77** before the handler fix.
9. Applied R1: BorrowingController now locally maps IllegalStateException to HTTP 409 with the exception message under `error`. R3 moved GET /members/{id}/borrowings into BorrowingController using explicit full endpoint paths; removed its prior MemberController mapping/injection. No service logic, entity, repository, POM, or original test assertions changed. README now documents the implemented Lab 2 endpoints and pending report gate.
10. `mvn test 2>&1 | Tee-Object -FilePath reports/evidence/lab2/03-review-green.txt` with exit-code capture and encoding-only UTF-8 normalization — **exit 0: 21 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS**. The two previous HTTP errors now pass.
11. `git diff --exit-code e629c25 -- src/main/java/com/example/libraryapi/service/BorrowingService.java src/test/java/com/example/libraryapi/service/BorrowingServiceTest.java src/test/java/com/example/libraryapi/service/BorrowingFineTest.java src/test/java/com/example/libraryapi/controller/MemberBorrowingControllerTest.java` — exit 0, unchanged. `git diff --exit-code 3fedd09 -- src/test/java/com/example/libraryapi/controller/BorrowingControllerTest.java` — exit 0, unchanged. `git diff --check -- . ':(exclude)reports/evidence/**'` — passed. Raw Maven output retains original trailing whitespace.
12. Created the four-section `reports/lab2-report.md` draft, with the complete review output and factual evidence. Human hours and reflections are visibly pending. Updated backlog story statuses to DONE based on passing behavior while preserving all actual hours as TBD. Added `docs/reference-labs-checklist.md` to map assignment steps, evidence, grading, and future dependency gates; this is planning, not Lab 3 implementation.
13. Verified the report's pending-input markers with `rg -n 'PENDING|TBD|calculateFine|Status|Grading|R[1-7]' reports/lab2-report.md docs/sprint-backlog.md`; checked raw-log results with `rg -n 'Tests run:|BUILD SUCCESS|BUILD FAILURE' reports/evidence/lab2/03-review-green.txt`; inspected source diffs and repeated the unchanged-service/test comparisons. `git rev-parse lab-1` still returns `452aaaf51e2aa233a3edfd047e6d9a5dfa176b03`.

## Current artifact inventory

- Modified production: BorrowingController.java (existing PUT endpoint GREEN, active GET mapping, local 409 handler), MemberController.java (remove relocated mapping/injection). BorrowingService and original assertions unchanged.
- Added tests: BorrowingFineUnitTest.java, BorrowingErrorControllerTest.java, BorrowingPersistenceTest.java.
- Updated documentation: README.md, docs/sprint-backlog.md (DONE behavior; actuals TBD), docs/lab2-xp-review.md, this evidence log.
- Added deliverable draft: reports/lab2-report.md (all four prescribed sections; human content clearly pending).
- Added planning: docs/reference-labs-checklist.md (all remaining labs' objectives, steps, artifacts, commands, rubric, evidence, and dependency gates).
- Actual raw outputs: reports/evidence/lab2/01-baseline.txt, 02-review-red.txt, 03-review-green.txt.
- Assignment DOCX and startup-error.txt remain untouched, untracked inputs.

## Git checkpoint policy

The next GREEN/report-draft commit is an intermediate Lab 2 checkpoint, not the final prescribed submission commit. Its own hash and pushed remote HEAD can be recovered with `git log --oneline --decorate`; a commit cannot contain its own resulting hash. Push/verification output will be retained separately and referenced from this log. No lab-2 tag will be created while actual effort and personal report sections are pending.

### Latest test inventory

| Class | Cases | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| BorrowingControllerTest | 1 | 0 | 0 | 0 |
| BorrowingErrorControllerTest | 6 | 0 | 0 | 0 |
| MemberBorrowingControllerTest | 1 | 0 | 0 | 0 |
| BorrowingFineTest | 5 | 0 | 0 | 0 |
| BorrowingFineUnitTest | 5 | 0 | 0 | 0 |
| BorrowingPersistenceTest | 1 | 0 | 0 | 0 |
| BorrowingServiceTest | 2 | 0 | 0 | 0 |
| **Total** | **21** | **0** | **0** | **0** |

### Errors and corrections

- Original sandbox writes to `.git/index.lock` and Maven's `.m2` cache were denied earlier in this session; approved escalated Git/Maven commands resolved those permission issues. No alternate cache was used to bypass permission controls.
- The return-endpoint RED test received HTTP 404 before the endpoint existed; the approved endpoint delegated to the existing service and turned that test GREEN (commit b1a382a).
- Review RED found two unhandled `IllegalStateException` HTTP errors; a controller-local conflict handler fixed them, preserving the service's exception behavior.
- PowerShell formats a Java class-sharing warning on stderr as `NativeCommandError` text in captured logs. Maven's actual exit code and summary are authoritative: both GREEN runs exited 0. No test failure was hidden or ignored.
- Windows PowerShell Tee-Object initially used UTF-16; logs were normalized to UTF-8 without trimming/replacing execution output. Git's trailing-whitespace notices in the raw logs are expected output formatting, not source-code check failures.

## Prompts and decisions

- User authorized completing Labs 2–8 sequentially against the original assignment, preserving tests/history, recording actual results, committing/tagging/pushing completed labs, and leaving personal input unfilled.
- Lab 2 is the only active implementation scope. Labs 3–8 were inspected for dependency planning; no future-lab source/artifacts have been implemented.
- Read-only audit found missing direct fine unit tests, no recorded Step D review, controller-placement mismatch, pending actual hours/velocity/report, and no final tag/push.
- Step D's exact review prompt, output, and proposed decisions are in `docs/lab2-xp-review.md`, recorded before applying improvements. R1–R4 are the proposed improvements; R5–R7 explain deferred work.
- Existing `com.example.libraryapi` packages and entity setters are retained. The professor's illustrative `com.library.api` test used unavailable constructors; its required behavior is preserved without migrating the project.
- Original estimates (3, 3, 2, 2 hours) remain unchanged. User requested actual hours remain TBD until they supply honest figures. Execution duration is not a substitute for personal effort.

## Screenshot checklist

No screenshot is explicitly required by Lab 2; these are optional useful evidence. No capture is claimed without a real image.

- [ ] Historical RED: show `git show 3fedd09` with the test-first diff and recorded HTTP 404 failure. This displays commit evidence, not a newly captured original terminal run.
- [ ] Review-improvement RED: capture the new failing HTTP-conflict test output before its fix, if the student is present at that stage; raw text will also be retained.
- [ ] Final GREEN: terminal showing `mvn test`, all test totals, and `BUILD SUCCESS`.
- [ ] Final backlog/report: editor showing supplied actual hours and velocity calculation, once available.
- [ ] Final Git: `git log --oneline --decorate` and `git ls-remote --tags origin lab-2` after completion/push.

## Completion gate

Lab 2 is **INCOMPLETE** until the four stories and tests, review improvements, actual effort/velocity, student reflections, report, final commit, `lab-2` tag, and push are verified. No Lab 3 implementation may start before that gate passes.
