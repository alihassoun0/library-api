# Lab 6 — Measures and Metrics

Project: Library Management API. Source: the complete original assignment's Lab 6 Steps A–G, five deliverable questions and grading table. Baseline: main `1cfca1b`, with lab-5 at `822e381`; **81 tests, 0 failures, 0 errors, 0 skipped** before edits. The pending Lab 4 report edit is preserved and excluded from this lab. Commit/tag/push require final approval. Lab 7 is not started.

## 1. Coverage report

JaCoCo 0.8.11 reports **290 covered / 306 total executable lines = 94.77% overall line coverage** after the quality fix. Before the fix the existing81-case suite covered **287/303 = 94.72%**. Both exceed the required40%; production code was not changed to reach that target. The three new tests exercise the actual map-ownership correction.

The overall percentage uses the root **LINE** counter, not the instruction percentage displayed prominently in JaCoCo's HTML. The HTML Total row shows16 missed lines out of306, agreeing with the XML. The local HTML report was opened successfully in isolated headless Edge, with the rendered coverage table and Total row present. Repositories are interfaces without executable lines, so the report measures26 instrumentable classes rather than all30 Java source types. JaCoCo's normal generated-record filtering is used; no custom coverage exclusions were added.

| Class | Covered lines | Total lines | Line coverage |
|---|---:|---:|---:|
| controller.BookController | 10 | 11 | 90.91% |
| controller.BorrowingController | 6 | 8 | 75% |
| controller.MemberController | 7 | 8 | 87.5% |
| controller.ReservationController | 9 | 9 | 100% |
| dto.BookDTO | 1 | 1 | 100% |
| dto.BorrowingDTO | 1 | 1 | 100% |
| dto.BorrowingRequestDTO | 1 | 1 | 100% |
| dto.ErrorResponseDTO | 4 | 4 | 100% |
| dto.MemberDTO | 1 | 1 | 100% |
| dto.ReservationDTO | 1 | 1 | 100% |
| dto.ReservationRequestDTO | 1 | 1 | 100% |
| entity.Book | 23 | 23 | 100% |
| entity.Borrowing | 23 | 23 | 100% |
| entity.Member | 17 | 17 | 100% |
| entity.Reservation | 17 | 17 | 100% |
| exception.ErrorResponseFactory | 3 | 3 | 100% |
| exception.GlobalExceptionHandler | 10 | 10 | 100% |
| LibraryApiApplication | 1 | 3 | 33.33% |
| mapper.BookMapper | 13 | 15 | 86.67% |
| mapper.BorrowingMapper | 16 | 18 | 88.89% |
| mapper.MemberMapper | 11 | 13 | 84.62% |
| mapper.ReservationMapper | 14 | 16 | 87.5% |
| service.BookService | 17 | 18 | 94.44% |
| service.BorrowingService | 43 | 43 | 100% |
| service.MemberService | 7 | 8 | 87.5% |
| service.ReservationService | 33 | 33 | 100% |

**Lowest:** LibraryApiApplication, **33.33% (1/3 lines)**. XML shows its generated constructor's line7 covered and main's lines10–11 missed. SpringBootTest starts application contexts without invoking the static main method. This explains the gap; it does not demonstrate a startup defect, and no artificial entry-point call was added solely to increase coverage.

Essential raw evidence is retained in `evidence/lab6/jacoco-before.xml` and `jacoco.xml`; browsable HTML remains at `target/site/jacoco/index.html`.

## 2. Manual cyclomatic complexity

For each method, CC = explicitly counted decision points + 1. Count `if`, `else if`, `for`, `while`, `do`, `case`, `catch`, `&&`, `||`, and `?`. Count each actual branch once; do not count an `else if` twice. No such compound conditions, loops, switch branches, catches or ternaries occur in these methods. Repository `.orElseThrow(...)` and lambda syntax are not decision tokens in the professor's list. Calls do not add the called method's CC to the caller.

Line references are to `src/main/java/com/example/libraryapi/service/BorrowingService.java`, unchanged by Lab 6.

| Method | Source lines | Decision evidence | Decision count | CC | Risk level (Chapter 6, p.60) |
|---|---|---|---:|---:|---|
| Constructor | 29–35 | No listed decision | 0 | 1 | Simple program, no real risk |
| `borrowBook(Long, Long)` | 38–59 | `if (!book.isAvailable())`, line 44 | 1 | 2 | Simple program, no real risk |
| `returnBook(Long)` | 62–72 | `if (borrowing.getReturnDate() != null)`, line 66 | 1 | 2 | Simple program, no real risk |
| `applyReturnState(Borrowing)` | 74–83 | No listed decision | 0 | 1 | Simple program, no real risk |
| `getActiveBorrowings(Long)` | 86–91 | `if (!memberRepository.existsById(memberId))`, line 87 | 1 | 2 | Simple program, no real risk |
| Public `calculateFine(LocalDate, LocalDate)` | 93–96 | Computes days and delegates; no listed decision | 0 | 1 | Simple program, no real risk |
| Private `calculateFine(long)` | 98–103 | `if (overdueDays <= 0)`, line 99 | 1 | 2 | Simple program, no real risk |

The overloaded fine methods are listed separately because Lab 5 preserved the original public API and extracted a private amount calculation. Every method has CC ≤2; none exceeds 5. Extracting helpers reduced the size of the public workflow, but does not remove the need to test each guard and the fine boundary.

The student supplied the official classifications from the professor's Chapter 6 lecture, page 60:

| Cyclomatic Complexity | Risk Level |
|---|---|
| 1–10 | Simple program, no real risk |
| 11–20 | Moderately complex and risky program |
| 21–50 | Complex and highly risky program |
| Greater than 50 | Untestable and extremely risky program |

All listed methods have CC1 or CC2 and therefore fall in the official 1–10 category: **Simple program, no real risk**. The existing decision counts and CC calculations are unchanged. This lecture label describes complexity risk; it does not establish the absence of all possible defects.

## 3. Coupling and instability

Counting scope: all production Java files under `src/main/java`, excluding tests and generated output. Ce is the number of distinct explicitly imported non-static types in a service file, including JDK types, project types, Spring types, interfaces and annotation types. All service imports are explicit; there are no wildcard/static imports to expand. Ignore automatic `java.lang` visibility, same-package references without imports, and fully qualified references without imports. Ca is the count of distinct production source classes explicitly importing that service. This is the assignment's import-based proxy; it is not runtime or transitive coupling. Test-class imports are intentionally excluded so test quantity does not inflate architectural stability.

| Class | Ce | Ca | I = Ce / (Ce + Ca) | Importing production class |
|---|---:|---:|---:|---|
| BookService | 7 | 1 | 0.8750 | BookController |
| BorrowingService | 14 | 1 | 0.9333 | BorrowingController |
| MemberService | 7 | 1 | 0.8750 | MemberController |
| ReservationService | 12 | 1 | 0.9231 | ReservationController |

Ce audit: BookService and MemberService each import one entity, one repository, HttpStatus, Service, Transactional, ResponseStatusException and List (7). BorrowingService imports three entities, three repositories, those four Spring types, BigDecimal, LocalDate, ChronoUnit and List (14). ReservationService imports three entities, three repositories, those four Spring types, LocalDate and List (12). The evidence document gives a reproducible source-counting command.

BorrowingService is the most unstable by this definition (14/15). It coordinates books, members and borrowings and is consumed by one HTTP controller, so a high dependency ratio is unsurprising for an application service. The number is not proof of a defect: annotation and date/math imports contribute to Ce, and the scope deliberately excludes tests. Additional abstraction should follow an actual change need rather than a desire to reduce this ratio artificially.

## 4. AI-generated versus manually written code

The project history and earlier reports document Codex-assisted generation of BorrowingService and ReservationService. Actual measurements show:

| Documented AI-assisted class | Line coverage | Import-based Ce / Ca / instability | Manual CC example |
|---|---|---|---|
| BorrowingService | 43/43 = 100% | 14 / 1 / 0.9333 | borrowBook2; returnBook2; fine overloads1 and2 |
| ReservationService | 33/33 = 100% | 12 / 1 / 0.9231 | Not manually analyzed in Step C |

These values show that full line execution can coexist with a comparatively high dependency ratio. Borrowing's extra date/math dependencies and its coordination of three resources explain part of the Ce difference; a bigger implementation is not automatically more productive or better designed. SLOC can increase through generated accessors, schemas and boilerplate without increasing delivered behavior. Lab5's measured line increase after introducing clearer abstractions already demonstrates why raw line growth alone is a weak productivity measure.

**Confirmed authorship and comparison limits:** the student confirms that almost all Java classes and methods were generated by Codex. The student supplied requirements, reviewed and approved changes, and verified functionality through testing. These are substantive engineering contributions, but they do not establish a separately measured manually written Java comparison group. The table therefore characterizes the predominantly Codex-generated implementation using actual coverage, coupling and CC values. It cannot establish metric differences between AI-generated and manually written code, because no verified manual-code cohort is available. No manual-code metrics or AI-versus-human productivity advantage are invented. The supported conclusion is that human requirements, review and testing remain necessary even when generated code achieves high coverage and low method complexity.

## 5. Complete GQM plan

The completed `docs/gqm.md` is reproduced below.

# GQM Measurement Plan

## Goal

Analyse the Library Management API in order to improve maintainability and regression confidence with respect to measured test coverage and confirmed static-analysis findings from the point of view of the developer.

## Questions

Q1: Does the existing regression suite execute enough production lines to meet the assignment's 40% overall line-coverage requirement without changing production code to inflate coverage?

Q2: Does correcting one confirmed important static-analysis finding remove that finding while preserving the established API behavior?

## Metrics

M1 (answers Q1): JaCoCo overall line coverage = covered lines / (covered + missed lines) × 100. Run `mvn test jacoco:report`, read the root LINE counter in `target/site/jacoco/jacoco.xml`, and compare the result with the HTML summary. Record class-level counters and the lowest-covered class as context. Use all production classes; do not add exclusions to reach the target.

M2 (answers Q2): Number of occurrences of the selected confirmed PMD/SpotBugs finding, identified by rule/type, class, and method/field in the raw report, before and after the correction. Rerun the same analyzer/configuration and the complete Maven test suite. Record total findings separately so successful removal of one finding is not misrepresented as removal of all findings.

## Results

M1 result: Before the quality fix, **287 covered / 303 total lines = 94.72%**. After the fix and focused regression cases, **290 / 306 = 94.77%**. The existing suite already exceeded 40% before any production correction. LibraryApiApplication is lowest at **1/3 = 33.33%**; its main method is not invoked by the Spring-context tests. No class was excluded to raise coverage.

M2 result: SpotBugs `EI_EXPOSE_REP2` on `com.example.libraryapi.dto.ErrorResponseDTO.<init>` (original reported line9), priority2/rank18: **1 occurrence before, 0 after** the defensive-copy correction. Total SpotBugs findings **11 ->10**. The separate accessor `EI_EXPOSE_REP` warning remains; the analyzer does not infer the constructor's immutable snapshot, and no suppression was added. PMD reports9 priority3 findings; configured `failOnViolation=false` means its check exits0 despite those findings. Post-fix full suite: **84 tests passed, 0 failures/errors/skips**, including all81 original tests and3 focused regression cases.

## Interpretation

The coverage minimum is met by a wide margin, but executing lines does not prove correct assertions or production reliability. Keep the independent fine-boundary and HTTP contract tests. The added tests protect a genuine snapshot contract, rather than serving a coverage target. Review the unexecuted main entry separately if deployment-entry testing becomes a requirement.

The selected constructor warning is gone with unchanged error JSON/status behavior. Review the10 remaining SpotBugs findings in framework context: JPA relationship accessors and injected services intentionally retain shared object identity and should not be “fixed” by blindly copying entities or dependencies. The ErrorResponseDTO accessor warning is retained as an analyzer limitation backed by the input/output mutation tests. PMD constructor recommendations also require JPA/Spring judgment. Suggested action: keep the verified snapshot fix, retain measured reports, and prioritize remaining findings by actual behavior and ownership. No Lab 7 coverage target or audit work was started.

## Quality improvement and regression safety

**Original finding:** SpotBugs `EI_EXPOSE_REP2`, ErrorResponseDTO constructor, original reported line9 (record field `fieldErrors` declared at14), priority2/normal, rank18. The XML message says the constructor may expose representation by storing an externally mutable map. Engineering assessment: IMPORTANT encapsulation/response-consistency issue because changing the input map after construction changes the response's error details. This is a project severity judgment, not a claim that SpotBugs labels the finding CRITICAL or high priority. PMD's nine priority3 constructor/entry-class findings were not artificially promoted to satisfy this step.

**Correction:** add a compact record constructor that snapshots non-null maps into a LinkedHashMap and wraps that copy with Collections.unmodifiableMap. Keep null maps unchanged, preserve iteration order and existing null entries, retain fields/schema annotations, and leave timestamps, statuses and messages unchanged. Unlike copying injected services or JPA entities, copying this String-to-String transport map has an appropriate value/snapshot boundary. Mutation of a DTO's exposed map is intentionally prevented; existing HTTP behavior is preserved.

```diff
 import java.time.Instant;
+import java.util.Collections;
+import java.util.LinkedHashMap;
 import java.util.Map;
 ...
         Map<String, String> fieldErrors) {
+
+    public ErrorResponseDTO {
+        if (fieldErrors != null) {
+            fieldErrors = Collections.unmodifiableMap(new LinkedHashMap<>(fieldErrors));
+        }
+    }
 }
```

**Verification:** the exact constructor `EI_EXPOSE_REP2` occurrence is **1 before ->0 after** rerunning SpotBugs on newly compiled classes. Total findings are **11 ->10**. The separate accessor `EI_EXPOSE_REP` warning remains: SpotBugs does not establish that every constructor path installs either null or an immutable snapshot. Runtime tests prove the non-null accessor cannot mutate the map. No suppression/filter removed the warning. Other JPA-reference and injection findings remain for contextual review; no “zero warnings” claim is made. PMD remains9 findings, all priority3.

**Regression tests:** ErrorResponseDTOTest adds3 cases: later input-map mutation cannot change the snapshot; callers cannot mutate the returned map; optional null field errors remain null. The snapshot test also preserves insertion order and null-valued entries. Existing MockMvc tests verify unchanged JSON, validation fields, timestamps and400/404/409/500 mappings. All81 original tests remain unchanged and passing; the complete post-fix suite has **84 passed, 0 failures, 0 errors, 0 skipped**. `mvn test jacoco:report` and the final standalone `mvn test` both run the entire suite. Full commands/results are in `docs/lab6-evidence.md`.

## Final grading checklist

| Professor's criterion | Points | Status |
|---|---:|---|
| PMD and SpotBugs XML reports in target | 2 | Generated; essential raw XML retained outside ignored target |
| Overall JaCoCo line coverage ≥40% | 2 | 94.77%; baseline before fix already94.72% |
| Correct manual CC table | 3 | Counts verified; official Chapter 6 page60 risk classifications applied |
| Complete measured/interpreted GQM | 3 | Goal,2 questions,2 metrics, actual results, interpretation/action complete |

No grade is self-awarded. Final submission uses the exact message `lab6: metrics — PMD, SpotBugs, JaCoCo, cyclomatic complexity analysis`, tag `lab-6`, and origin push only after approval. Lab 2's missing `lab-2` tag remains noted; previous commits/tags are preserved.

The student has approved this final submission, including the exact commit message, tag and push. The fresh precommit suite again passed84 tests with zero failures/errors/skips. Git outcomes are verified after execution; no production or metric changes were made during final submission.
