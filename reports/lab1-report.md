# Lab 1 — Introduction to AI-Assisted Engineering

**Project:** Library Management API  
**AI Development Tool:** OpenAI Codex CLI 0.167.0  
**Course:** Software Engineering (2026/2027)

## 1. Observation

Codex made several important design decisions during the project. First, it organized the application into separate entity, repository, and controller packages, which I agree with because this structure improves organization and separation of concerns. Second, it used `LocalDate` for date fields and `BigDecimal` for fine amounts; I agree because these types are appropriate for representing dates and monetary values. Third, it used unidirectional `ManyToOne` relationships between Borrowing, Book, and Member, which simplifies the data model and avoids circular references during JSON serialization.

## 2. Engineering Judgment

Accepting the generated code without reviewing it could have introduced unnecessary business rules, incorrect configurations, or implementation choices that did not match the professor's requirements. For example, the initial Codex plan included additional constraints such as unique ISBN and email values, which were not requested in the specification. I asked Codex to remove these unnecessary constraints and keep the implementation simple. I also verified the Maven build, sample database records, HTTP responses, and Swagger documentation.

## 3. Chapter Connection

Codex can assist with programming-in-the-small by generating individual classes, methods, and algorithms. However, it also supports programming-in-the-large by helping with project structure, implementation planning, integration, and testing. Programming-in-the-large becomes a collaboration between humans and AI agents in the AI era. Codex does not eliminate the need for software engineering because humans remain responsible for requirements, design decisions, validation, and the overall quality of the system.

## 4. Role Reflection

The role that best describes my work is the Junior Software Engineer in the AI Era, who prompts AI to scaffold a feature and then reads and understands the generated code. I provided Codex with a detailed specification, reviewed its implementation plan, requested corrections, and verified the generated application through compilation and API testing. I also reviewed and tested the AI-generated output before committing it. This experience showed me that AI can accelerate implementation, but understanding and verifying generated code remain essential human responsibilities.