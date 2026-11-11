# Lab 8 — Methods and actual evidence

## Sources and recovery

Read the original DOCX Lab8 Steps A–F, five report questions,3/3/2/2 rubric and final-course checklist. Inspected current main at `affff78342f87e124097c3156556c0927eb626dc`/lab-7, existing reports/backlog/evidence, current API source and available conversation records. Prior tags lab-1,lab-3,lab-4,lab-5,lab-6,lab-7 exist; lab-2 does not. Pending Lab4 report and untracked assignment/startup files remain excluded. No production code changes, new project or Git submission is authorized before approval.

Recoverable original forecast: backlog's3/3/2/2-hour Lab2 story estimates total10. No original personal forecasts for other labs were recovered; mark them not recorded, without substituting assignment durations. The student supplied approximately2 hours for Lab1, reconstructed from memory, and explicitly clarified approximately3 hours for the entire Lab2 as a retrospective estimate, not measured time. Earlier story figures2/1.5/1/1 sum to approximately5.5 but are not a reliable overall effort measurement. Preserve them only as historical figures; use approximately3h against the unchanged10h original estimate, yielding an approximate row ratio0.30. Updated the Lab2 report/backlog and Lab8 effort table consistently at the student's explicit request, without redistributing3 hours across stories. Labs3–7 remain untracked/unknown and Lab8 remains in progress. No effort is derived from Maven/agent durations or Git timestamps.

The student supplied `C:/Users/USER/Downloads/MonChap8_EstimationCout.pdf` (46 pages; SHA256 `EEFAAB2FD6CDAE61C393BF9C75ADC4DE4928A729198D299AC70E211064C389EA`). Extracted text with pypdf into ignored target. A sandboxed install could not access the package source; an approved reader installation under target and approved PDF extraction resolved permissions. No application dependency or production config was changed. Lecture pages23–29 define inquiry/input/output/internal/shared-interface counting and weights; pages15–16 give organic COCOMO and152-hour months; pages31–32 give area-based EQF.

## Compact inspection and calculations

Commands: `git status --short`, recent decorated log, `git tag --list`, `rg` over reports/docs for effort/formula records, source route/field inspection and a full30-file source read. Working source listing, snapshot hashes and arithmetic results stay under ignored target. The report contains the complete14-endpoint/four-entity/two-interface inventory; no separate inventory or calculation prose files are created.

Count every business endpoint once:8 EI,6 EQ,0 EO. Stored-data GET/filter operations are inquiries per lecture pages24–25; the computed fine is already part of the return EI. Use the assignment's stated H2/OpenAPI interface convention: one simple H2 connector and one average shared OpenAPI contract; do not count each table or schema endpoint again. A stricter logical-data boundary excluding the internal H2 connector is disclosed as a5-FP sensitivity, not silently mixed into the main calculation. Complexity judgments are proposed for student review based on actual fields/relations/rules; final student approval owns those choices.

Weight calculations: EI3×3+3×4+2×6=33; EO0; EQ4×3+2×4=20; ILF2×7+2×10=34; interfaces1×5+1×7=12. Total **99 UFP**. Estimated days99/9=11; hours11×7.5=82.5. No duplicate transaction outputs, error responses, DTOs, repositories or hypothetical fine-payment functions are added.

Physical source size uses the assignment's exact scope `src/main/java` and LF semantics equivalent to `wc -l`:

```powershell
$files = @(Get-ChildItem src/main/java -Recurse -Filter '*.java')
$lines = 0
foreach ($file in $files) {
    foreach ($byte in [System.IO.File]::ReadAllBytes($file.FullName)) {
        if ($byte -eq 10) { $lines++ }
    }
}
# Observed: $files.Count=30, $lines=1249; every file ends in LF.
```

Total actual effort A remains incomplete: LOC/hour=1249/A and FP/hour=99/A are formulas only, with no numerical project rates. The clarified approximately3h is only Lab2's actual estimate; the superseded5.5-hour story sum is not used as a total or denominator. Predominantly Codex-generated authorship and student requirements/review/approval/testing are already confirmed in the conversation.

## Lecture formula verification and COCOMO

DeMarco EQF was verified directly on lecture pages31–32: area under actual cost divided by area between forecast and actual cost, `(A×sum Δt)/sum(|E_j−A|×Δt_j)`. Lecture example numerator40.25, denominator4.75; arithmetic8.4737, despite8.7 printed on the slide. Absolute differences are needed to obtain the stated denominator. The lecture does not define a separate “AI velocity multiplier”; v=E/A is stated only as a separate possible convention. Only Lab2's supported approximate row ratio3/10=0.30 is calculated. Numerical multipliers and project totals/ratios remain withheld because the project records are incomplete.

No dated revisions to a total-project forecast were recovered. For one fixed forecast E, area cancellation would give EQF_fixed=A/|E−A|, but that remains an unconfirmed approximation, not a computed personal result. Earlier numerical Lab2 EQF/multiplier illustrations remain removed; the clarification does not supply project totals or forecast timing. No final total, project EQF, velocity, productivity rate or modified numerical alpha is calculated while the necessary records are incomplete. The lecture's independent worked example and base COCOMO arithmetic remain unchanged.

Hypothetical Fine Payment scope and exclusions are in the report, with component-based production-source ranges: record/repository50/80/130; service100/150/240; controllers/DTOs70/110/170; glue40/80/140. Totals260/420/680 are estimates, not implemented/measured code. Weighted size(260+4×420+680)/6=436.6667; KLOC0.4366667; HM2.4×KLOC^1.05=1.0054692303; TDEV2.5×HM^0.38=2.5051869854; average team HM/TDEV=0.4013549632;152-hour conversion=152.831322999. No payment gateway/auth infrastructure is silently included. Small-size model extrapolation is stated. Proposed alpha scaling2.4×A/E is a local assumption awaiting actual effort, not an observed new calibration.

## Regression, preservation and final-course checklist

Ran `mvn -Djacoco.append=false test jacoco:report` on main, captured in `target/lab8-baseline-verification.log`: **106 tests,0 failures,0 errors,0 skipped, exit0, BUILD SUCCESS**, completed2026-11-11 12:56:35 +02:00. Actual JaCoCo root LINE296/306=96.73%; service102/102=100%, exceeding the prior80% service requirement. Existing live reports and retained prior-lab evidence remain intact. Source/test/POM/Lab4 report hashes are saved in `target/lab8-before.json` for final preservation checks.

- Completed independent work: full function inventory and reasoned UFP judgments, FP effort calculation, measured source count, lecture formula verification, hypothetical module/COCOMO, factual lifecycle reflection and final tests/coverage.
- Lab2 discrepancy resolved by the student's explicit clarification: approximately3h total actual from memory,10h original estimate unchanged;5.5h is a historical story sum only. Pending: Labs3–7 remain untracked/unknown; record Lab8 actual after completion. Original forecasts for Labs1,3–8 remain not recorded. Project totals/ratios/velocity/EQF/modified alpha/productivity stay withheld unless supported by complete appropriate data. Lab1's approximately2 hours is recorded without inventing a forecast. No Lab8 completion or grade is claimed while these gaps remain.
- Final-course gaps remain visible: missing lab-2 tag; pending revised Lab4 report outside this submission. Earlier Docker/Swagger evidence is retained; no new live Docker deployment is asserted in this estimation lab.
- Intended submission: Lab8 report, this evidence document, checklist, reconciled existing sprint backlog and the expressly requested Lab2 report correction (5 files). No extra supporting bundles or optional screenshot blocking. No production/tests/POM changes. Exact commit message `lab8: FP estimation, COCOMO analysis, effort retrospective`; new lab-8 and origin push only after final file review and approval. All earlier tags/history must remain unchanged.
