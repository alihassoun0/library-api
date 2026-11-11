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
