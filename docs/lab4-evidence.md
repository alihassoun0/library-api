# Lab 4 — Design, implementation, deployment evidence

Authoritative source: SE_Lab_Assignment_2026_2027_Codex.docx, complete Lab4 paragraphs249–303, StepsA–F, all four report questions and rubric, read by ZIP/XML with line breaks preserved. Objective: refine design/patterns/separation and build a deployable container. Learning objectives: Repository/Service/DTO patterns, design flaw identification, first-class OpenAPI, Docker build/run.

## Authorization and initial inspection

User authorized completing all Lab4 technical steps, preserving prior behavior/tests/history/tags, using current dependencies, reporting real evidence, and withholding commit/tag/push until final staged-file review/approval. No Lab5 work is authorized. This supersedes the historical Lab3 prohibition on controller edits for this explicitly assigned DTO migration; docs/AGENTS.md is retained unchanged.

Commands: git status --short; git log -4 --oneline --decorate; git tag --list; rg --files src; Get-Content POM/application configuration/AGENTS.md; Get-ChildItem/Get-Content over the entire source tree and tests; Get-Command docker,curl.exe,java,mvn,wsl. HEAD/origin main at2d5a1d4; tags lab-1 and lab-3 exist; lab-2 absent. Only the professor DOCX and startup-error.txt are unrelated untracked inputs; exclude them.

## Step A review

Full pre-change review is reports/evidence/lab4/design-review.md and will be pasted into the report. CRITICAL: controller business/persistence orchestration, direct repository coupling, all four controllers exposing entities. IMPORTANT: inconsistent global errors/validation and missing explicit OpenAPI metadata. MINOR: duplicated test fixtures, deferred to avoid Lab5 work.

## Baseline and preservation

Created reports/evidence/lab4. Ran `mvn test` with Tee-Object capture and Maven exit-code preservation; converted only file encoding to UTF8. reports/evidence/lab4/01-baseline.txt: exit0, **68 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**. Captured SHA256 of all tracked src files/POM in00-before-manifest.json. Existing tests/assertions and entities must remain unchanged; intentional controller/config changes are reviewed separately.

## Docker prerequisite diagnosis (before implementation)

`docker version` reported client29.8.0 and missing dockerDesktopLinuxEngine pipe. `docker context show`: desktop-linux. Sandboxed `wsl --status` returned E_ACCESSDENIED; approved outside-sandbox rerun showed docker-desktop default distribution/version2. No Docker Desktop/backend process was present. The WSL1 optional-component message is not itself evidence that WSL2 is unavailable.

`docker desktop start --help` confirmed --detach; sandbox log access was denied. Approved `docker desktop start --detach` exited0: Starting Docker Desktop. Backend readiness/build/run still require actual verification; starting the app alone is not deployment evidence.

## Implementation decisions

Preserve exact nested JSON via BookDTO/MemberDTO within BorrowingDTO and ReservationDTO; no JPA types in controller signatures/responses. Add mappers for all four resources. Move Book/Member orchestration to services. Replace local error handlers with one ControllerAdvice; preserve ResponseStatusException404 as well as the newly assigned mappings. Use current Springdoc/validation/test dependencies; no POM additions. Do not add constants/refactorings reserved for Lab5.

## Pending execution and submission gates

Tests, running OpenAPI/UI verification, Docker build/run/curl and report/checklist must be verified before readiness. Commit/tag/push remain unauthorized until the requested final staged-list review. No deployment, screenshots, new test totals, commit hash or tag is claimed yet.

## Screenshot checklist

- StepA review: editor showing the severity table and actual code references.
- Final Maven: terminal summary with actual counts/BUILD SUCCESS.
- Swagger: running /swagger-ui.html redirected UI, expanded resource groups, schemas/examples and status responses; use saved /api-docs JSON as complementary evidence.
- Docker: real build completion; running container/port8080; `curl http://localhost:8080/books` response. Capture only after actual success.
- Final Git: approved staged list, lab-4 tagged commit and remote verification after separate approval.

No screenshot is fabricated; pending captures will not stop independent work.

## Actual implementation and verification

Created BookDTO, MemberDTO, BorrowingDTO, ReservationDTO, BorrowingRequestDTO,
ReservationRequestDTO, ErrorResponseDTO; BookMapper, MemberMapper,
BorrowingMapper, ReservationMapper; BookService, MemberService; and
GlobalExceptionHandler. Updated all four controllers to use DTO signatures,
services/mappers, Tag/Operation/ApiResponses. Removed local error handlers in
favor of ControllerAdvice. Updated only the two assigned Springdoc paths in
application.properties. Added the exact Dockerfile and .dockerignore. Updated
README for reservation routes, DTO/errors, /api-docs and Docker commands.

ReservationDTO preserves all five existing fields and nested BookDTO/MemberDTO;
BorrowingDTO preserves all seven fields including nested DTOs, null returnDate
and BigDecimal fine. The three success paths are unchanged for reservations:
POST201, GET200, member-scoped DELETE204 soft cancellation. Existing entity,
repository, borrowing/reservation-service and test source is unchanged.

### Executed commands and real results

1. `mvn test` -> reports/evidence/lab4/01-baseline.txt: exit0, 68/0/0/0.
2. SHA256 manifest from `git ls-files src pom.xml` ->00-before-manifest.json.
3. `mvn test` after DTO migration ->02-dto-regression.txt: exit0,68/0/0/0.
4. `docker info --format '{{.ServerVersion}}'` ->29.8.0;
   `wsl --list --verbose` ->docker-desktop Running, version2 after backend start.
5. `docker build -t library-api:lab4 .` captured through Tee-Object ->
   03-docker-build.txt: exit0, Maven BUILD SUCCESS, image exported. The stage ran
   exactly `mvn package -DskipTests`; skipped execution is not presented as tests.
6. Added ApiDesignContractTest6 and GlobalExceptionHandlerTest7. The latter uses
   a mocked BookService only for synthetic mapping cases; its assigned borrowing
   flows still use the real BorrowingService/repositories/database. The original
   68 cases and assertions were not edited.
7. `mvn test` ->04-full-tests.txt: exit0, **81 tests,0 failures,0 errors,0 skipped**.
   No source/test failures occurred. The build context preceded these new test
   files, but includes the final unchanged production source/config/Dockerfile.
8. `docker run -p 8080:8080 library-api:lab4` started container1e4523711063,
   vibrant_lamarr. Startup log confirms Tomcat8080 and application started.
   Container remains running intentionally for review, with the prescribed heap.
9. `curl.exe --fail --silent --show-error --dump-header reports/evidence/lab4/05-books-headers.txt --output reports/evidence/lab4/05-books.json http://localhost:8080/books`
   ->HTTP200 and exactly five sample books. Windows curl.exe avoids PowerShell's
   curl alias; this is the actual assigned HTTP verification, with capture flags.
10. curl.exe with --location/--dump-header/--output retrieved /swagger-ui.html
    ->302 to /swagger-ui/index.html then200. Saved06-swagger-headers.txt and
    06-swagger-ui.html. Retrieved /api-docs ->07-openapi.json and
    /api-docs/swagger-config ->08-swagger-config.json; UI config points /api-docs.
11. Parsed live OpenAPI to09-operation-inventory.json: **14 operations**, all
    summaries/tags and declared statuses; schema names are exactly the seven
    DTO types, with no Book/Member/Borrowing/Reservation entity schemas.
12. `docker ps --filter ancestor=library-api:lab4 --format '{{json .}}'` showed
    running container/port8080; image inspect confirmed its image identity and
    entrypoint. Saved `docker inspect 1e4523711063` to10-container-inspect.json,
    `docker logs 1e4523711063` to11-container-logs.txt, and `docker image inspect
    library-api:lab4` to12-image-inspect.json. Actual image identity:
    sha256:fa83484d3e354321d0a36a37afb41c0820f26c86aec7c9fac775f0737b1972ce.
13. Checked installed Edge/Chrome paths. Approved isolated headless Edge:
    --headless=new --disable-gpu --no-first-run --disable-extensions,
    new temporary user-data-dir, --window-size=1600,2200,
    --virtual-time-budget=15000, --screenshot, --dump-dom,
    http://localhost:8080/swagger-ui.html. Exit0. Saved13-swagger-ui.png,
    13-swagger-rendered.html, 13-browser-stderr.txt. Used view_image to visually
    inspect the real screenshot: all14 operation headers,4 groups and7 schemas.
    No existing user browser profile was accessed. This verifies the running
    browser UI, not merely that static HTML is reachable.
14. Initial DOM counting regex assumed an exact CSS class order and counted0;
    screenshot inspection showed correct rendered content. Corrected only the
    verification regex to count `<div class="opblock opblock-` and tag headings:
    **14 operation blocks,4 tags**. No application fix or test failure is invented.
15. Compared20 protected entity/repository/domain-service/original-test/POM
    hashes against the manifest: **0 changed**. Parsed actual Surefire XML into
    14-final-verification.json:81 cases,0 failures/errors/skipped; original68
    still pass. `rg` found no entity/repository imports or local ExceptionHandler
    in controllers; ApiDesignContractTest additionally checks all signature and
    DTO field types. `git diff --check` passes source/doc changes.
16. Created reports/lab4-report.md with all four required questions, full initial
    review, actual test/Docker/UI results, and deployment analysis. Updated
    docs/reference-labs-checklist.md with the actual Lab4 rubric/evidence.

### Problems and corrections

- Docker stopped backend: diagnosed missing Linux engine pipe, distinguished
  sandbox WSL denial from engine state, started Docker Desktop with approval,
  then confirmed running WSL2/backend and actual build/run. No deployment claim
  was made until real container/HTTP verification.
- Windows stderr formatting labels ordinary Maven/JVM/Docker progress as
  NativeCommandError in captured text. Actual exit codes were retained and used;
  all three host Maven runs and the Docker build succeeded. Nothing was hidden.
- PowerShell Tee-Object default encoding is UTF16; actual stdout/stderr text was
  normalized to UTF8 for Git readability without stripping Maven output.
- The DOM-count regex was corrected after visual verification; retained real
  DOM/screenshot establish the result. No source bug or false failed test is
  asserted. No dependency changes or additional libraries were necessary.

### Final tests

| Group | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| Existing Lab1–3 cases | 68 | 0 | 0 | 0 |
| ApiDesignContractTest | 6 | 0 | 0 | 0 |
| GlobalExceptionHandlerTest | 7 | 0 | 0 | 0 |
| Total | 81 | 0 | 0 | 0 |

### Screenshots and approval status

- [x] Captured and inspected actual running Swagger overview:13-swagger-ui.png.
- [ ] Optional student capture: expand a Swagger operation to show its status
  table/DTO examples; the saved live JSON already records the documentation.
- [ ] Optional capture: Docker Desktop showing vibrant_lamarr running on8080,
  alongside the real HTTP200 /books output and81-case GREEN summary.
- [ ] Final Git screenshot after approval, commit/tag/push and remote verification.

Technical rubric evidence is verified; final staging review/approval and commit,
lab-4 tag, push are pending. No commit hash or tag status is fabricated. No Lab5
code, metrics, refactoring experiments or artifacts were created.

### Final file review preparation

Inspected git diff --stat/status and the Dockerfile/DTO source; repeated
git diff --exit-code on original borrowing/reservation tests/domain services
and POM: unchanged. git diff --check passed tracked source/doc edits.
git show-ref --tags confirms lab-1 at452aaaf51e2aa233a3edfd047e6d9a5dfa176b03
and lab-3 at2d5a1d40eba71ced30e0ffe5bbe6ff69605dd119, unchanged. Staging is
limited to the four controller updates, Springdoc configuration, new DTO/mapper/
handler/Book+Member-service source, two new tests, Docker artifacts, README,
report, checklist and this lab's evidence. The original assignment DOCX and
startup-error.txt remain excluded. The exact staged list will be shown to the
student; no commit, tag or push occurs until the student's subsequent approval.

Actual staging used explicit approved Lab4 paths, then inspected
`git diff --cached --name-only`, `--stat`, and
`git diff --cached --check -- . ':(exclude)reports/evidence/lab4/**'`.
Source/doc checks passed; raw evidence output retains its original formatting.
The final48-file staged list is reports/evidence/lab4/15-staged-files.txt,
including that list itself. The only unstaged inputs remain the assignment
DOCX and startup-error.txt. No commit/tag/push command has been issued.

## Final submission approval

The student's subsequent actual instruction was: "I approve the Lab 4 final
submission." They explicitly requested reinspection of the48-file index,
exclusion of unrelated inputs, exact required commit message, lab-4 on that
commit, origin push, remote main/tag verification and preservation of lab-1,
Lab2 progress history and lab-3. This approval supersedes earlier pending
submission markers, which describe the prior implementation stage.

At final submission, git status, cached/unstaged name lists, recent log,
show-ref --tags, origin URLs, the saved48-file list and actual81-case GREEN
log were inspected again. No production/test change was made for submission;
only approval-status documentation is updated. No new Maven run is claimed.
No Lab5 work is authorized or started. Final commit/tag/push outcomes must be
verified after the commit; actual results will be reported to the student and
saved locally under target/lab4-submission-verification.json without pretending
they existed before this commit.
