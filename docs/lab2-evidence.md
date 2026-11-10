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
