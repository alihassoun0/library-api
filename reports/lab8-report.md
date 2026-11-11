# Lab 8 – Estimation and Cost

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**Instructor:** Rima Kilany Chamoun  
**AI Development Tool:** OpenAI Codex CLI

# 1. Function Point Analysis

Function Point Analysis (FPA) is a software measurement technique used to estimate the functional size of an application based on the services it provides rather than the number of lines of code.

For this laboratory, the analysis covers the completed Library Management API, including books, members, borrowings, and reservations. The hypothetical Fine Payment module is excluded because it has not been implemented.

## 1.1 Function Point Categories and Weights

The application was analyzed using the five categories specified in the assignment.

| Element | Simple | Average | Complex |
|---|---:|---:|---:|
| External Inputs (EI) | 3 | 4 | 6 |
| External Outputs (EO) | 4 | 5 | 7 |
| External Inquiries (EQ) | 3 | 4 | 6 |
| Internal Logical Files (ILF) | 7 | 10 | 15 |
| External Interface Files (EIF) | 5 | 7 | 10 |

External inputs represent operations that modify application data. External outputs provide processed information, while external inquiries retrieve information without modifying stored data. Internal logical files represent the application's main data groups, and external interface files represent its technical interfaces.

## 1.2 Function Inventory and Complexity

Each operation was classified according to its fields, relationships, and business rules.

### External Inputs

| Operation | Complexity | FP |
|---|---|---:|
| POST /books | Simple | 3 |
| PUT /books/{id} | Average | 4 |
| DELETE /books/{id} | Simple | 3 |
| POST /members | Simple | 3 |
| POST /borrowings | Average | 4 |
| PUT /borrowings/{id}/return | Complex | 6 |
| POST /reservations | Complex | 6 |
| DELETE /members/{memberId}/reservations/{reservationId} | Average | 4 |
| **Total** | | **33** |

The borrowing and reservation operations have higher complexity because they involve additional business rules, such as book availability, member eligibility, fine calculation, and duplicate reservation checks.

### External Outputs and Inquiries

All six existing GET operations were classified as external inquiries because they retrieve stored information without performing a separate business calculation.

| Operation | Complexity | FP |
|---|---|---:|
| GET /books | Simple | 3 |
| GET /books/{id} | Simple | 3 |
| GET /members | Simple | 3 |
| GET /members/{id} | Simple | 3 |
| GET /members/{id}/borrowings | Average | 4 |
| GET /members/{memberId}/reservations | Average | 4 |
| **Total inquiries** | | **20** |

No separate calculated external output was counted because the current API does not contain a dedicated calculated-report endpoint.

### Internal Logical Files

| Entity | Complexity | FP |
|---|---|---:|
| Book | Simple | 7 |
| Member | Simple | 7 |
| Borrowing | Average | 10 |
| Reservation | Average | 10 |
| **Total** | | **34** |

### External Interface Files

| Interface | Complexity | FP |
|---|---|---:|
| H2 Database Integration | Simple | 5 |
| OpenAPI / Swagger | Average | 7 |
| **Total** | | **12** |

H2 and OpenAPI are counted according to the interface convention explicitly provided in the professor's assignment.

## 1.3 Unadjusted Function Point Calculation

| Element | Simple | Average | Complex | Weighted FP |
|---|---:|---:|---:|---:|
| External Inputs | 3 × 3 | 3 × 4 | 2 × 6 | 33 |
| External Outputs | 0 × 4 | 0 × 5 | 0 × 7 | 0 |
| External Inquiries | 4 × 3 | 2 × 4 | 0 × 6 | 20 |
| Internal Logical Files | 2 × 7 | 2 × 10 | 0 × 15 | 34 |
| External Interfaces | 1 × 5 | 1 × 7 | 0 × 10 | 12 |
| **Total UFP** | | | | **99** |

Therefore:

UFP = 33 + 0 + 20 + 34 + 12

**Total UFP = 99 Function Points**

This represents the estimated functional size of the current Library Management API based on the selected counting assumptions.

# 2. Function Point Effort Estimation

The professor provides a productivity reference of 8–10 Function Points per person-day for a junior developer working with modern development tools.

For this calculation, the midpoint of 9 Function Points per person-day is used. A working day contains 7.5 hours.

## 2.1 Estimated Development Effort

Estimated Days = UFP / Productivity

Estimated Days = 99 / 9

**Estimated Days = 11 person-days**

Estimated Hours = Estimated Days × 7.5

Estimated Hours = 11 × 7.5

**Estimated Effort = 82.5 person-hours**

## 2.2 Productivity Sensitivity Analysis

| Productivity | Person-Days | Person-Hours |
|---|---:|---:|
| 8 FP/day | 12.375 | 92.81 |
| 9 FP/day | 11.000 | 82.50 |
| 10 FP/day | 9.900 | 74.25 |

The estimated effort ranges from approximately 74.25 to 92.81 person-hours, with 82.5 hours as the midpoint.

These values are model-based estimates rather than measurements of the time actually spent on the project.

# 3. Estimated Versus Actual Effort

The following table compares the approximate time spent completing the eight laboratories.

The actual hours are retrospective estimates reconstructed from memory, rather than formally recorded time measurements.

## 3.1 Actual Effort Across the Eight Labs

| Lab | Topic | Actual Hours (Approx.) |
|---|---|---:|
| 1 | Introduction to AI-Assisted Development | 2 |
| 2 | Agile Methodologies and TDD | 3 |
| 3 | Requirements and Specification | 2 |
| 4 | Design, Implementation and Deployment | 3.5 |
| 5 | Refactoring | 2.5 |
| 6 | Measures and Metrics | 2 |
| 7 | Software Testing | 1.5 |
| 8 | Estimation and Cost | 1 |
| **Total** | | **17.5 hours** |

The Lab 8 figure represents the approximate effort recorded for the work completed during preparation of this report.

## 3.2 Lab 2 Estimation Comparison

Lab 2 is the only laboratory for which an original personal estimate was recovered.

Original estimated effort = 10 hours

Approximate actual effort = 3 hours

Actual-to-estimated ratio:

3 / 10 = **0.30**

This indicates that the retrospectively estimated actual effort was approximately 30% of the original forecast.

The earlier story-level figures totaling 5.5 hours were not a reliable measurement of the total elapsed time and were therefore not used as the actual total.

The other original personal forecasts were not formally recorded. Consequently, a complete project-wide actual-to-estimated ratio and velocity multiplier cannot be calculated reliably.

## 3.3 DeMarco Estimation Quality Factor

The professor's lecture defines the Estimation Quality Factor (EQF) as:

**EQF = Area under the actual-cost curve / Area between the estimated-cost and actual-cost curves**

For a final actual cost A and a fixed estimate E maintained over a period T, an illustrative fixed-estimate expression is:

EQF = (A × T) / (|E − A| × T)

The duration cancels:

**EQF = A / |E − A|**

However, the complete project does not have recorded original forecasts and dated estimate revisions for all eight laboratories. Therefore, a reliable project-wide EQF cannot be calculated.

The available Lab 2 ratio is not a substitute for the complete project estimation history.

This limitation demonstrates the importance of recording initial forecasts and updating estimates throughout the development process.

# 4. COCOMO 81 – Hypothetical Fine Payment Module

The professor proposes extending the Library Management API with a Fine Payment module.

The proposed module would allow members to view outstanding fines, allow librarians to record payments, and generate monthly fine reports.

This module is hypothetical and was not implemented in the current application.

## 4.1 Functional Requirements

| Endpoint | Description |
|---|---|
| GET /members/{id}/fines | Retrieves outstanding fines belonging to a member. |
| POST /fines/{id}/pay | Allows a librarian to mark an unpaid fine as paid. |
| GET /reports/fines?month=YYYY-MM | Generates a monthly summary of fines. |

The existing borrowing records already contain fine amounts. For this estimate, each fine is associated with one borrowing record, and the borrowing ID identifies the fine.

A separate FinePayment record could store the borrowing reference and payment date. A unique borrowing reference would help prevent duplicate payments.

The monthly report would distinguish between fines assessed during a month and payments recorded during that month.

The estimate excludes payment gateway integration, a new authentication system, and historical accounting functionality.

## 4.2 Three-Point LOC Estimation

The hypothetical module was estimated using minimum, best-fit, and maximum lines of code.

| Component | Minimum LOC | Best-fit LOC | Maximum LOC |
|---|---:|---:|---:|
| Payment entity, repository and queries | 50 | 80 | 130 |
| Service logic and validation | 100 | 150 | 240 |
| Controllers and DTOs | 70 | 110 | 170 |
| Mapping and error handling | 40 | 80 | 140 |
| **Total** | **260** | **420** | **680** |

The minimum estimate assumes straightforward implementation and reuse of existing components. The best-fit estimate includes normal validation, payment checks, and monthly calculations. The maximum estimate allows for more complex queries and integration work.

## 4.3 Weighted LOC Estimate

The three-point estimate is calculated using:

Estimated LOC = (Minimum + 4 × Best-fit + Maximum) / 6

Estimated LOC = (260 + 4 × 420 + 680) / 6

Estimated LOC = 2620 / 6

**Estimated Size = 436.67 LOC**

## 4.4 Convert LOC into KLOC

KLOC = LOC / 1000

KLOC = 436.67 / 1000

**KLOC = 0.43667**

## 4.5 COCOMO Effort Calculation

The Organic COCOMO 81 model uses:

- α = 2.4
- β = 1.05

Effort = α × KLOC^β

Effort = 2.4 × (0.43667)^1.05

**Effort ≈ 1.00547 person-months**

Using 152 working hours per person-month:

Estimated Hours = 1.00547 × 152

**Estimated Effort ≈ 152.83 person-hours**

## 4.6 Development Duration

TDEV = 2.5 × Effort^0.38

TDEV = 2.5 × (1.00547)^0.38

**TDEV ≈ 2.50519 months**

## 4.7 Average Team Size

Average Team Size = Effort / Development Duration

Average Team Size = 1.00547 / 2.50519

**Average Team Size ≈ 0.40135 FTE**

### COCOMO Results Summary

| Metric | Result |
|---|---:|
| Weighted project size | 436.67 LOC |
| Development effort | 1.00547 person-months |
| Effort in hours | 152.83 hours |
| Development duration | 2.50519 months |
| Average team allocation | 0.40135 FTE |

These are theoretical estimates for the hypothetical extension, not actual development measurements.

# 5. AI Productivity Analysis

Software productivity can be evaluated using different measurements. This project uses Lines of Code per hour and Function Points per hour to examine the relationship between the amount of software produced and the effort spent.

## 5.1 Project Size

The current Library Management API contains:

| Measurement | Result |
|---|---:|
| Number of Java source files | 30 |
| Physical Java source lines | 1,249 |
| Unadjusted Function Points | 99 |

The source-code count includes imports, annotations, accessors, comments, and blank lines.

## 5.2 LOC per Hour

LOC per hour measures the number of physical source-code lines produced per hour.

LOC/hour = Total Source Lines / Total Actual Hours

LOC/hour = 1,249 / 17.5

**LOC/hour ≈ 71.37**

## 5.3 Function Points per Hour

Function Points per hour measure the functional size delivered relative to the time spent.

FP/hour = Total UFP / Total Actual Hours

FP/hour = 99 / 17.5

**FP/hour ≈ 5.66**

These values are approximate because the actual hours were reconstructed from memory rather than formally recorded.

## 5.4 Interpretation

Almost all Java classes and methods in the project were generated by Codex. Therefore, Lines of Code alone do not provide a complete picture of development productivity.

AI can generate large amounts of source code quickly, but additional lines do not necessarily represent additional functionality or better software quality.

For example, the project gained 22 additional test methods during Lab 7 without increasing the production source-code line count. This strengthened verification without increasing the measured production LOC.

For this project, FP/hour provides a more meaningful indication of productivity because it measures the functionality delivered rather than the amount of code generated.

Function Points provide a more consistent basis for evaluating the actual features delivered, although they still depend on accurate complexity classifications and effort estimates.

Neither metric alone is sufficient to evaluate software quality. Testing results, maintainability, and correctness must also be considered.

# 6. Final Reflection on the Software Engineering Lifecycle

The eight laboratories demonstrated how the different stages of the software engineering lifecycle work together to develop and improve a software application.

Throughout this project, OpenAI Codex assisted with generating code, creating tests, identifying problems, performing refactoring, and analyzing software quality. However, the development process still required clear requirements, human review, and verification.

**Requirements and Specification**

Lab 3 demonstrated the importance of defining requirements before implementation. Writing business rules and acceptance criteria for the reservation feature helped establish clear expectations and made it easier to verify whether the implemented functionality was correct.

**Design and Implementation**

Labs 1 and 4 focused on building the application and improving its architecture. The introduction of service layers, DTOs, mappers, and global exception handling helped separate responsibilities and improve maintainability.

**Refactoring and Quality Improvement**

Lab 5 demonstrated that existing code can be improved without changing its intended behavior. Extracting methods, replacing magic numbers, and centralizing repeated logic made the application easier to maintain.

**Testing and Measurement**

Labs 6 and 7 highlighted the importance of software quality measurement and testing. Static analysis, code coverage, and deliberate bug experiments helped identify potential weaknesses and evaluate whether the tests could detect incorrect behavior.

The final test suite contained 106 passing tests, with 96.73% overall line coverage and 100% service line coverage.

**Estimation and Cost**

Lab 8 connected the completed application to software estimation techniques. Function Point Analysis and COCOMO 81 provided ways to estimate functional size and development effort.

The comparison also demonstrated that Lines of Code are not always a reliable productivity measure when AI generates most of the implementation.

**Overall Experience with AI-Assisted Development**

Using Codex made implementation faster and helped reduce repetitive programming work. However, it did not eliminate the need to understand the requirements, review generated code, identify errors, and verify the final results.

The most important lesson from this project is that AI can support the software development process, but it cannot replace engineering judgment. The developer remains responsible for the quality, correctness, and reliability of the final application.

# 7. Conclusion and Final Results

The eight software engineering laboratories provided practical experience in developing, improving, testing, and evaluating the Library Management API.

The project progressed from an initial REST API into a more structured application with borrowing, returning, reservations, DTOs, global exception handling, API documentation, and Docker deployment.

The final project results are summarized below:

| Area | Final Result |
|---|---|
| Functional size | 99 Unadjusted Function Points |
| Function Point effort estimate | 11 person-days (82.5 hours) |
| Production Java files | 30 |
| Physical Java source lines | 1,249 |
| Approximate actual effort | 17.5 hours |
| Final automated test suite | 106 passed |
| Overall line coverage | 96.73% |
| Service line coverage | 100% |
| Hypothetical Fine Payment module | 436.67 estimated LOC |
| COCOMO development effort | 1.00547 person-months |

The Function Point estimate concerns the current application, while the COCOMO calculation concerns a hypothetical extension. They represent different scopes.

The use of AI assistance accelerated code generation and helped with testing and analysis. However, requirements definition, reviewing generated code, validating results, and making engineering decisions remained essential throughout the project.

One important lesson was that successful software development cannot be judged by the amount of code generated alone. Functional correctness, maintainability, meaningful testing, and reliable verification are equally important.

Overall, this project demonstrated how AI-assisted development can support the software engineering lifecycle when combined with proper planning, human supervision, and systematic testing.
