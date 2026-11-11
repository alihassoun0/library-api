# Lab 6 — Execution and verification evidence

## Scope, original assignment and preserved state

Read the complete Lab6 section of `SE_Lab_Assignment_2026_2027_Codex.docx` using .NET ZipFile/XML, including nested table paragraphs: Steps A–G, all5 report questions, the GQM template and the2/2/3/3-point grading rubric. `docs/AGENTS.md` is the historical Lab3 reservation specification; the explicit current Lab6 request authorizes the plugins and targeted correction. No Lab7 work is included.

Initial `git status --short`: pending modified `reports/lab4-report.md`; untracked assignment DOCX and `startup-error.txt`. Initial HEAD/main and locally recorded origin/main `1cfca1b`; tags lab-1, lab-3, lab-4, lab-5. Lab2's missing final tag remains outstanding. No commit/tag/push is authorized before final review. Source/POM/current Lab4 report hashes were captured locally in ignored `target/lab6-before.json` before edits.

Compact artifact strategy: this evidence document, the five-question report, required `docs/gqm.md` and checklist update, plus **five essential XML outputs** under `reports/evidence/lab6/`: `pmd.xml`, `spotbugs-before.xml`, `spotbugs-after.xml`, `jacoco-before.xml`, `jacoco.xml`. Working logs, rendered HTML, calculation JSON and preservation snapshots remain in ignored target; no duplicate per-step prose files, full source dumps or screenshots are committed. Required live reports remain `target/pmd.xml`, `target/spotbugsXml.xml`, `target/site/jacoco/index.html`.

## Executed commands and results

All timestamps below are2026-11-11, local+02:00. Maven output was redirected through `cmd /c` to avoid PowerShell stderr formatting; the actual `$LASTEXITCODE` was checked immediately. No `|| true` masked a result.

| Stage / command | Actual outcome | Local working log |
|---|---|---|
| Repository inspection: `git status --short`, recent log, tag list, POM, historical spec, full assignment | Existing project/history retained; Lab4 report isolated | Conversation and snapshot |
| Baseline `mvn test` before edits | Exit0; **81 tests,0 failures,0 errors,0 skipped**, BUILD SUCCESS;12:08:42 | `target/lab6-baseline-tests.txt` |
| First `mvn pmd:pmd` | Exit1: sandbox denied creation under `.m2/repository/com/github/spotbugs` while resolving plugin prefixes | `target/lab6-pmd-before.log` |
| Approved outside-sandbox retry `mvn pmd:pmd` | Exit0; PMD6.55.0; **9 priority3 findings**;12:10:37 | `target/lab6-pmd-before-retry.log` |
| `mvn -B spotbugs:spotbugs` | Exit0; **11 findings**, all priority2/rank18;12:13:15 | `target/lab6-spotbugs-before.log` |
| `mvn -B test jacoco:report` before correction | Exit0; **81/0/0/0**; LINE287/303 =94.72%;12:14:14 | `target/lab6-coverage-before.log` |
| `mvn -B pmd:check` | Exit0, BUILD SUCCESS; explicitly reports **9 violations**, because failOnViolation=false;12:14:54 | `target/lab6-pmd-check.log` |
| Initial SpotBugs invocation immediately after source edit | Exit0 but goal did not compile source; not accepted as fix verification | `target/lab6-spotbugs-after.log` |
| `mvn -B test jacoco:report` after correction | Compiled corrected DTO and new tests; exit0; **84/0/0/0**; LINE290/306 =94.77%;12:15:53 | `target/lab6-final-tests-coverage.log` |
| `mvn -B pmd:pmd spotbugs:spotbugs` against compiled corrected code | Exit0; PMD9 findings; SpotBugs11->10; selected constructor finding1->0;12:21:24 | `target/lab6-analysis-after-compiled.log` |
| Final standalone `mvn test` | Exit0, BUILD SUCCESS; **84 tests,0 failures,0 errors,0 skipped**; automatic JaCoCo test-phase report also ran;12:23:50 | `target/lab6-final-tests.log` |

The exact requested plugin versions/configuration are in POM: PMD3.21.0, failOnViolation=false, printFailingErrors=true, rulesets/java/quickstart.xml, targetJdk17; SpotBugs4.8.3.1, Max/Low/failOnError=false; JaCoCo0.8.11 with prepare-agent and report bound to test phase. Explicit `jacoco:report` runs in addition to the bound report, so those logs show two report executions without running tests twice. Plugins add build analysis, not application dependencies.

## Confirmed finding, severity judgment, correction and residuals

Original raw `spotbugs-before.xml`: `EI_EXPOSE_REP2`, class `com.example.libraryapi.dto.ErrorResponseDTO`, method `<init>`, field `fieldErrors`, source line9 (record's declaration; map component at14), priority2/normal, rank18. Message: constructor may expose representation by storing an externally mutable map. The neighboring `EI_EXPOSE_REP` accessor finding is a separate occurrence. Engineering assessment: **IMPORTANT**, because an error DTO should represent the validation result at creation; changing an aliased input map could otherwise change its serialized result. SpotBugs' own priority/rank is retained and is not falsely called high/critical.

Changed only this production class: a compact record constructor copies non-null maps to a LinkedHashMap and wraps the copy in Collections.unmodifiableMap. This preserves insertion order, null entries and optional null maps, existing record fields/schema metadata and JSON/status/message/timestamp behavior. It intentionally prevents mutation of the response's map. The report contains the targeted code difference.

Added `src/test/java/com/example/libraryapi/dto/ErrorResponseDTOTest.java`,3 genuine regression cases: snapshot survives later input mutation and retains order/null values; accessor rejects mutation; optional null map remains null. Original11 test files/assertions remain unchanged. Existing HTTP/error tests still cover serialization and field-error/status behavior. Baseline81 -> final84 reflects these3 new cases, not deleted, skipped or weakened tests.

Verify by parsing BugInstance nodes and matching **type EI_EXPOSE_REP2 + class ErrorResponseDTO + constructor**: before1, after0. Total counts11->10. The first verifier incorrectly expected both constructor and accessor warnings to disappear; actual XML showed only the selected constructor finding gone. The report was corrected to record that distinction. The accessor warning remains despite the immutable snapshot: the analyzer does not establish the constructor invariant. Runtime mutation tests verify that invariant. No suppressions, filters, disabled rules or priority changes were used to claim success.

Remaining warnings concern injected BookService identity, JPA Book/Member relationship accessors/setters, and the DTO accessor. Blind defensive copying of JPA entities or injected dependencies could change intended semantics and was not performed. PMD still reports9 medium-priority findings: UseUtilityClass on the Spring Boot entry class and UnnecessaryConstructor/UncommentedEmptyConstructor pairs on4 JPA entities. These require framework-aware review rather than manufactured high-severity fixes.

## Measurement methods and audit trail

**Coverage:** parse the direct root LINE counter of JaCoCo XML; percentage=100×covered/(covered+missed). Baseline287/(287+16)=94.71947%; final290/(290+16)=94.77124%. The full26-class line-counter table and lowest-class explanation are in the report. Interfaces without executable code do not produce line coverage; default generated-record filtering is retained without custom exclusions. This is line coverage, not the HTML instruction percentage.

Opened the final local HTML report in isolated headless Edge using a new target profile and `--dump-dom`; Start-Process with -Wait/hidden window returned exit0, rendered6977 bytes, title library-api, coverage table and Total row present. First direct GUI-binary launch did not yield a usable captured DOM and was not treated as browser proof. Captured final DOM is `target/lab6-browser-dom.html`. No existing browser profile was accessed. Screenshot capture is optional and not a blocker.

**Manual CC:** inspect unchanged BorrowingService line-by-line, following the professor's decision-token list. Decisions at44,66,87,99; one in each corresponding method. Constructor and helper/public fine wrapper have none. Both overloaded calculateFine methods are distinguished. Lambda and orElseThrow calls are not listed decision tokens. No method has CC>5. The student subsequently supplied the official Chapter6 lecture page60 bands: 1–10 **Simple program, no real risk**; 11–20 **Moderately complex and risky program**; 21–50 **Complex and highly risky program**; greater than50 **Untestable and extremely risky program**. Applied these exact labels to the unchanged CC1/CC2 values for every listed method. No genuine counting error was found and no CC calculation was changed.

**Coupling:** production-only scope, distinct explicitly imported types, including framework/JDK/annotation/interface types; Ca counts distinct production source classes explicitly importing a service. No test imports, wildcard expansion, java.lang automatic imports or transitive dependencies. Reproducible counting core, executed interactively in PowerShell:

```powershell
$sources = Get-ChildItem src/main/java -Recurse -Filter '*.java'
foreach ($service in (Get-ChildItem src/main/java/com/example/libraryapi/service -Filter '*.java')) {
    $text = Get-Content -Raw -LiteralPath $service.FullName
    $imports = @([regex]::Matches($text, '(?m)^import\s+([\w.]+);') |
        ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique)
    $name = 'com.example.libraryapi.service.' + $service.BaseName
    $importers = @(foreach ($source in $sources) {
        if ((Get-Content -Raw -LiteralPath $source.FullName) -match
            ('(?m)^import\s+' + [regex]::Escape($name) + ';')) { $source.BaseName }
    })
    # Ce=$imports.Count; Ca=$importers.Count; I=Ce/(Ce+Ca).
}
```

Observed Ce/Ca: Book7/1, Borrowing14/1, Member7/1, Reservation12/1. Import sets and importer identities are audited in the report; working full calculation is `target/lab6-coupling.json`. The correction adds DTO imports only and does not change service coupling.

**Authorship:** the student subsequently confirmed that almost all Java classes/methods were generated by Codex; the student provided requirements, reviewed and approved changes, and verified functionality through testing. The report now attributes these roles explicitly. Actual measured coverage/coupling/CC values characterize that predominantly generated implementation. No separately verified manually written Java cohort exists for comparison, so no manual-code metrics or AI-versus-human differences are claimed. This is a supported comparison limitation, not an outstanding request to invent a manual cohort. The official lecture risk bands and authorship clarification resolve the earlier documentation questions without changing measured results.

## Preservation, screenshots and submission

Only existing production file changed: ErrorResponseDTO.java; POM is configured; one new3-case test file is added. All original tests, controllers, services, entities, repositories, mappers, resources and Docker artifacts are preserved. Lab4 report retains its initial hash. Git history/tags are unchanged; lab-2 remains absent and lab-6 is not created. Final approval is required for the exact commit `lab6: metrics — PMD, SpotBugs, JaCoCo, cyclomatic complexity analysis`, tag and push.

Optional screenshots: JaCoCo overall/class table (select line counts rather than instruction bars); original/updated SpotBugs finding panels with exact rule/class/method; final84-case Maven summary; final Git refs after approval. Valid tables/raw XML are retained, so screenshots do not stop technical completion.

## Approved final submission

The student approved the final Lab6 submission, exact professor commit message, lab-6 tag, origin push and verification of remote main/tag plus preservation of previous tags. This supersedes earlier pending-approval markers, which describe preparation. Reinspected the12 intended paths: POM, corrected DTO, new3-case test, GQM, report, evidence/checklist and five retained raw XML reports. Validated XML and reviewed hashes/GQM consistency; pending Lab4 report, assignment DOCX and startup-error.txt are excluded. All retained evidence and previous test files are preserved.

Fresh precommit `mvn test`: **84 tests,0 failures,0 errors,0 skipped, exit0, BUILD SUCCESS**, finished2026-11-11 12:35:05 +02:00. Working log `target/lab6-submission-tests.log`; approved file list `target/lab6-submission-files.txt`. Fetch confirmed no new origin/main commits before submission. Only approval-status documentation changed during this submission turn; production/tests/analysis XML remain as reviewed. Inspect the exact12-path index, then execute the approved commit/tag/push. Resulting refs are verified afterward and recorded separately in ignored `target/lab6-submission-verification.json`; future Git outcomes are not asserted in this precommit snapshot. Lab7 remains unstarted.
