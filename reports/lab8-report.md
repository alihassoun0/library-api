# Lab 8 — Estimation and Cost

Library Management API. This report uses the original Lab8 assignment and the professor's `MonChap8_EstimationCout.pdf`: function points on PDF pages23–29, productivity on30, DeMarco EQF on31–32, and organic COCOMO on15–16. Function-point complexity and hypothetical LOC are estimates for student review; code size, tests and coverage are measured. Missing effort figures are not replaced by Maven duration or Git elapsed time.

## 1. Function points and estimated effort

The inventory includes all14 business endpoints, four entities and the two technical interfaces named in the assignment. Each endpoint is counted once. A POST/PUT response or error does not become an additional output; nested Book/Member fields are not extra logical files. DTOs, mappers and repositories do not add entities. The hypothetical Fine Payment module is excluded.

Lecture page24 defines an inquiry as a request/response that reads data without a processing algorithm; page25 distinguishes outputs produced by an algorithm. All six current GET business endpoints retrieve stored data, including member/active filters, so they are classified as inquiries. **There is no separate calculated GET output to count as EO.** Fines are calculated by the already-counted return input. JSON mapping is representation, not an additional business calculation.

The lecture gives qualitative complexity categories rather than a mandatory field/relationship matrix. The judgments below use actual writable/logical fields, referenced records and rules. Final student approval confirms these proposed judgments.

| Type | Element | Complexity | FP | Actual fields/rules and justification |
|---|---|---|---:|---|
| EI | POST /books | Simple | 3 | Six writable book fields; supplied ID ignored; simple creation/save. |
| EI | PUT /books/{id} | Average | 4 | Path ID plus six replacement fields; lookup/404 and preserve stored identity. |
| EI | DELETE /books/{id} | Simple | 3 | One path ID, existence check and deletion; no new domain calculation. |
| EI | POST /members | Simple | 3 | Four writable member fields; supplied ID ignored; creation/save. |
| EI | POST /borrowings | Average | 4 | Two IDs; resolve book/member, reject unavailable book, mark it unavailable, save14-day loan. No invented inactive-member borrowing restriction. |
| EI | PUT /borrowings/{id}/return | Complex | 6 | One ID but several state/calculation rules: lookup, repeat-return rejection, date, overdue-day charge, zero boundary, restored availability and persistence. |
| EI | POST /reservations | Complex | 6 | Two IDs; book/member existence, unavailable-book policy, active member, duplicate-active rejection, generated ID/date/active state; retain other records unchanged. |
| EI | DELETE /members/{memberId}/reservations/{reservationId} | Average | 4 | Two IDs, member-scoped lookup, repeated-cancellation conflict and soft cancellation. |
| EQ | GET /books | Simple | 3 | Retrieve records with seven Book fields; no derived business values. |
| EQ | GET /books/{id} | Simple | 3 | One ID, lookup/404, seven stored fields. |
| EQ | GET /members | Simple | 3 | Retrieve records with five Member fields. |
| EQ | GET /members/{id} | Simple | 3 | One ID, lookup/404, five stored fields. |
| EQ | GET /members/{id}/borrowings | Average | 4 | Member existence and null-return-date filter; seven logical loan fields including two nested resources (17 scalar response values). |
| EQ | GET /members/{memberId}/reservations | Average | 4 | Member existence and active filter; five logical reservation fields including two nested resources (15 scalar response values). |
| EO | Calculated GET reports | None implemented | 0 | No monthly/derived report exists in the current API; do not double-count the inquiries above. |
| ILF | Book | Simple | 7 | Seven fields; one logical catalog record. |
| ILF | Member | Simple | 7 | Five fields; one logical member record. |
| ILF | Borrowing | Average | 10 | Seven logical fields, two relationships, return lifecycle and monetary state. |
| ILF | Reservation | Average | 10 | Five logical fields, two relationships, duplicate-active and soft-cancellation state. |
| EIF/interface | H2 integration | Simple | 5 | One configured local database/JDBC integration. Count the connector once under the assignment's explicit H2-interface convention, not the four tables again. |
| EIF/interface | OpenAPI/Swagger contract | Average | 7 | One shared generated contract:14 operations and seven DTO schemas, consumed by Swagger UI. UI redirect, schema URL and configuration URL are not separate business transactions. |

**Boundary convention:** the assignment explicitly names H2 and OpenAPI as external interfaces, so this is a course-level interface count. H2 stores application-owned internal files; it is not an additional externally maintained business-data file. A stricter logical-data boundary would exclude its5 interface points and give94 rather than99 UFP. The main calculation consistently follows the assignment's stated interface convention while counting every entity and endpoint once. The difference is a declared boundary assumption, not five extra business functions.

| Element | Simple count × weight | Average count × weight | Complex count × weight | Count | Weighted FP |
|---|---:|---:|---:|---:|---:|
| External inputs | 3×3 | 3×4 | 2×6 | 8 | 33 |
| External outputs | 0×4 | 0×5 | 0×7 | 0 | 0 |
| External inquiries | 4×3 | 2×4 | 0×6 | 6 | 20 |
| Internal logical files | 2×7 | 2×10 | 0×15 | 4 | 34 |
| External interfaces | 1×5 | 1×7 | 0×10 | 2 | 12 |
| **Total UFP** | | | | | **99** |

Using the required9 FP/person-day midpoint:

- Estimated days =99/9 = **11 person-days**.
- Estimated hours =11×7.5 = **82.5 person-hours**.
- Reference8–10 FP/day implies approximately9.90–12.375 days, or74.25–92.8125 hours. These are model estimates, not hours actually spent.

## 2. Estimated versus actual hours, EQF and velocity

Only Lab2's original personal estimate could be recovered: the backlog records3+3+2+2=10 estimated hours. Other original personal estimates are **not recorded** in the available backlogs/reports. Professor-provided durations are not substituted for personal forecasts.

The student recalls **approximately2 hours for Lab1**, estimated from memory, and has now clarified **approximately3 hours for the entire Lab2**, also a retrospective estimate from memory, not measured time. The earlier2+1.5+1+1 story figures sum to approximately5.5 hours but are not a reliable measure of total elapsed lab effort; they are retained only as historical figures and are not used as the actual total. The original Lab2 estimate remains10 hours. Its report and backlog are updated consistently, without inventing a new per-story allocation. Labs3–7 were not formally timed, and no precise actuals are inferred. Lab8 remains in progress; its actual effort will be recorded after completion from the student's record or clearly labeled recollection.

| Lab | Original personal estimate (h) | Actual effort (h) | Actual/estimated |
|---|---|---|---|
| 1 | Not recorded | Approximately2; reconstructed from memory | Not calculated—no personal forecast |
| 2 | 10 | Approximately3; clarified retrospective estimate from memory, not measured | Approximately0.30 |
| 3 | Not recorded | Not formally tracked; unknown | Not calculated |
| 4 | Not recorded | Not formally tracked; unknown | Not calculated |
| 5 | Not recorded | Not formally tracked; unknown | Not calculated |
| 6 | Not recorded | Not formally tracked; unknown | Not calculated |
| 7 | Not recorded | Not formally tracked; unknown | Not calculated |
| 8 | Not recorded | In progress; record after completion | Not calculated |
| **Eight-lab total** | **Incomplete** | **Incomplete** | **Withheld** |

Only the supported Lab2 row ratio is calculated: approximately3/10=0.30. No final project effort total, total estimation ratio or numerical velocity multiplier is calculated from the incomplete eight-lab data. Once compatible records exist, row ratios use r_i=A_i/E_i and the total ratio uses r=A/E, not the mean of row ratios. A speed/velocity multiplier may be defined as v=E/A=1/r, separately from DeMarco EQF; no numerical multiplier is asserted. Missing original forecasts remain missing even if retrospective actuals are later supplied.

### Exact DeMarco formula verified from the lecture

Pages31–32 define **EQF = area under actual cost / area between estimated and actual cost**. For a final actual cost A and estimate history E(t) over duration T:

`EQF = (A × T) / integral_0^T |E(t) − A| dt`.

For piecewise estimates E_j held for intervals Δt_j: `EQF = (A × sum Δt_j) / sum(|E_j − A| × Δt_j)`. Larger values mean closer estimates. This is not A/E or E/A. The lecture example uses40.25/4.75; direct arithmetic is8.4737, although the slide prints8.7. The area denominator uses absolute differences even though its printed example omits absolute-value signs.

No dated project-forecast revisions or interval durations were recovered. Eight separate lab estimates are not automatically a history of revisions to one project forecast. If a **single fixed-estimate approximation** is explicitly chosen, E(t)=E throughout, duration cancels and `EQF_fixed=A/|E−A|`; equal estimate and actual give zero error area and an unbounded factor. This derivation is not a computed project result. No numerical project or Lab2 EQF is calculated while the necessary forecast history or approximation is unconfirmed; project effort totals also remain incomplete. The worked arithmetic above belongs only to the professor's lecture example.

## 3. LOC/hour, FP/hour and AI productivity

Measured scope matches the assignment's commands: **src/main/java only**, excluding tests, target and the hypothetical extension. There are **30 Java files and1,249 physical source lines**. The line counter counts LF characters, equivalent to wc -l; all files end with a newline. It includes imports, annotations, accessors, comments and blanks, so it is not a count of executable statements.

With A equal to the eventual total actual hours across all eight labs:

- LOC/hour = **1,249/A**.
- FP/hour = **99/A** under the declared course boundary.
- Numerical project rates remain withheld because total actual effort is incomplete. Lab2's approximately3 hours covers only that lab; the historical5.5-hour story sum is not used as an actual-effort denominator.

Almost all Java was generated by Codex, as the student confirmed. Many lines are accessors, DTO metadata and framework wiring; another generator could express the same behavior in fewer lines. Lab7 added22 meaningful test cases while production line count stayed unchanged. A high LOC/hour could therefore reward boilerplate and ignore review/testing. FP/hour is a fairer view of delivered functions because it counts catalog, borrowing and reservation behavior, but it still depends on the boundary and complexity judgments. Neither number alone measures correctness, maintainability or the student's understanding. Total effort should include requirements, review, testing, measurement and reporting, consistent with lecture page30.

## 4. Hypothetical Fine Payment module and COCOMO81

This is requirements/estimation only; no module is implemented. Proposed scope:

- `GET /members/{id}/fines`: return the existing member's positive outstanding returned-loan fines;404 for a missing member. Assume one fine per borrowing and fine ID=borrowing ID. Read stored fine amounts rather than charging a second fine.
- `POST /fines/{id}/pay`: librarian records payment of an existing unpaid fine, with a payment date and a unique borrowing reference in a small FinePayment record. Return404 for a missing fine and409 if already paid; save atomically to avoid duplicate payments. This records settlement; no card processor or actual money transfer is included. Librarian authorization is a requirement/assumption, not a claim that the current unauthenticated API already enforces roles.
- `GET /reports/fines?month=YYYY-MM`: validate the month; report assessed amounts by return date and paid amounts by payment date, with counts/totals using BigDecimal. Distinguish assessed and paid cohorts; do not claim a historical month-end balance without a ledger.

Reuse the existing entities/services/DTO/error architecture. New authentication infrastructure, gateway integration, historical accounting and PostgreSQL deployment are excluded; including them would require a new estimate.

These are bottom-up **production-source estimates**, excluding test code, comments and blank lines. They are not measured changes or a claim that physical project lines equal COCOMO source instructions.

| Component | Minimum LOC | Best-fit LOC | Maximum LOC | Estimate rationale |
|---|---:|---:|---:|---|
| Payment record/repository and queries | 50 | 80 | 130 | One small record, relation/uniqueness mapping and outstanding/paid queries |
| Service rules, totals, validation and transaction | 100 | 150 | 240 | Existence/payment checks, duplicate handling, money/date aggregation |
| Three controller operations and response/request DTOs | 70 | 110 | 170 | Three routes, status mapping and a few transport types |
| Mapping, error/configuration glue | 40 | 80 | 140 | Fit the existing mapper/advice setup and integration details |
| **Total** | **260** | **420** | **680** | Optimistic reuse through extra query/integration complexity |

Three-point size = (min+4×best+max)/6 = (260+4×420+680)/6 = **436.6667 LOC**.

KLOC =436.6667/1000 = **0.4366667**. Lecture pages15–16 confirm organic α=2.4, β=1.05, duration constants2.5 and0.38, and **152 hours/person-month**:

- Effort HM =2.4×0.4366667^1.05 = **1.005469 person-months**.
- Duration TDEV =2.5×1.005469^0.38 = **2.505187 months**.
- Average team =HM/TDEV = **0.401355 FTE**.
- Model effort in hours =1.005469×152 = **152.8313 hours**.

The fractional team is an average allocation, not a proposal to hire0.4 of a person. This is a sub-KLOC extension, so the old model's duration/effort output is a rough extrapolation, not a promised schedule. Its152-hour month and the FP reference's7.5-hour day are separate stated conventions. The estimated extension is also a different scope from the existing99-FP application; their hour figures should not be treated as two measurements of the same work.

### Proposed AI-adjusted alpha

Keep β=1.05. Under the explicit assumption that a complete, compatible personal effort ratio r=A/E transfers proportionally to this module, a possible adjustment is **α_AI=2.4×r=2.4×A/E=2.4/v**. Then HM_AI=r×1.005469; revised duration would be recomputed as2.5×HM_AI^0.38, not scaled linearly. These are conditional formulas only. **No modified numerical alpha or adjusted effort is calculated** from incomplete project hours or missing original forecasts. Lab2's clarified approximate ratio alone is insufficient for a project-wide calibration. Any eventual adjustment would be a local sensitivity assumption, not a statistically validated replacement for COCOMO's1980s calibration.

## 5. Lifecycle reflection

Across the project I supplied requirements, reviewed and approved changes, and checked the results through tests while Codex generated most Java code. The reservation specification made expected behavior explicit; DTOs and services separated design responsibilities; refactoring improved structure without changing behavior. Static analysis and coverage made quality measurable, and the two deliberate bugs showed why an assertion must check the right result. Estimation now brings these activities together: source size and passing tests are known, but time figures must come from honest records or memory. AI changed how code and tests were produced, but it did not remove responsibility for the specification, design choices, review or final submission. This reflection describes the recorded work, rather than invented feelings or unmeasured time savings.

## Grading and remaining inputs

| Professor's criterion | Points | Status |
|---|---:|---|
| UFP table and complexity reasons | 3 | Complete proposed99-FP count; explicit endpoint/interface boundary for student judgment |
| Estimated/actual hours for all8 labs | 3 | **Incomplete:** Lab1 approximately2h; Lab2 clarified approximately3h against10h original estimate; Labs3–7 untracked; Lab8 in progress; other original forecasts not recorded |
| Correct COCOMO with numbers | 2 | Complete base model; numerical personal-alpha adjustment awaits effort totals |
| Reflection connects AI to lifecycle | 2 | Complete factual reflection |

Final regression check: **106 tests passed,0 failures/errors/skips**; overall line coverage **96.73%**, services **100%**. Production/test code, POM, previous history/tags and pending Lab4 report are preserved. Lab2's missing final tag remains a final-course gap. Lab8 is not declared finished while effort inputs are missing. Exact submission message: `lab8: FP estimation, COCOMO analysis, effort retrospective`; commit/tag/push require final review and approval.
