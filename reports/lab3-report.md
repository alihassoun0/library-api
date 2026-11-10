# Lab 3 – Requirements and Specification

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Specification Quality Audit

The objective of this lab was to define clear requirements for a Book Reservation feature before starting implementation. A specification was created in `docs/AGENTS.md`, describing the feature, its business rules, entity fields, REST endpoints, and acceptance criteria.

The specification contains eight business rules:

| Rule | Description | Classification |
|---|---|---|
| 1 | Books, members, and reservations must exist when required. Missing records return HTTP 404. | Precise |
| 2 | Only unavailable books can be reserved. Available books return HTTP 409. | Precise |
| 3 | Only active members can create reservations. | Precise |
| 4 | A member cannot have two active reservations for the same book. | Partially ambiguous |
| 5 | New reservations receive a generated ID, today's date, and active status. | Precise |
| 6 | Members can only see their own active reservations. | Precise |
| 7 | Cancellation changes the reservation's active status to false without deleting it. | Precise |
| 8 | Reservation operations must not modify books, members, or borrowing records. | Precise |

Seven rules were sufficiently clear for implementation and testing. Rule 4 had one ambiguity concerning simultaneous reservation requests. The specification did not define how concurrent requests should be handled, so this behavior was not guaranteed.

The feature uses a Reservation entity with five fields: `id`, `book`, `member`, `reservationDate`, and `active`.

Three REST endpoints were implemented:

- `POST /reservations` – Create a reservation.
- `GET /members/{memberId}/reservations` – Retrieve active reservations for a member.
- `DELETE /members/{memberId}/reservations/{reservationId}` – Cancel a reservation.

Four acceptance criteria were defined and tested. They covered rejecting reservations for available books, successfully creating reservations, retrieving active reservations, and cancelling reservations.

The comparison between the specification and implementation showed that all four acceptance criteria were satisfied. The remaining ambiguity concerned concurrency and the priority of errors when multiple conditions were invalid at the same time.

## 2. AI as a Requirements Partner

Before implementation, the project requirements and existing code structure were reviewed. The specification was then completed and reviewed before the reservation feature was developed.

Codex confirmed its understanding of the feature and explained the planned implementation, including the entity, repository, service, controller, and tests.

One important decision was the cancellation endpoint. The proposed approach was discussed and changed to use a member-specific DELETE endpoint. This made the cancellation operation more clearly connected to the member who owns the reservation.

The clarification process also helped define important details that could otherwise have been misunderstood, such as:
- Whether cancellation deletes a reservation or simply deactivates it.
- What happens when a member tries to cancel another member's reservation.
- The difference between invalid input and a resource that does not exist.

This showed that providing a detailed specification before implementation helps avoid misunderstandings and makes the expected behavior easier to verify.

## 3. Use-Case Connection

Requirements can be divided into functional and non-functional requirements.

**Functional requirements** describe what the system must do. In this lab, they included creating reservations, checking book availability, verifying member eligibility, listing active reservations, and cancelling reservations.

**Non-functional requirements** describe technical and quality constraints on how the system is developed. Examples included preserving the existing project structure, using the existing dependencies, maintaining separation between application layers, and avoiding modifications to existing entities and controllers.

Functional requirements were easier to express as acceptance criteria because their expected results could be tested directly.

For example, when a member tries to reserve an available book, the system must return HTTP 409 with the specified error message. This provides a clear expected result that can be checked through an automated test.

Technical constraints were also important because they helped preserve the existing application and avoid unnecessary changes.

## 4. Spec-Driven vs. Vibe-Driven Development

In Lab 1, the project was initially developed using a general prompt describing the desired Library Management API. Although the result was functional, some implementation decisions needed additional review and correction.

In Lab 3, the Book Reservation feature was developed using a structured specification before implementation. The requirements included eight business rules, three REST endpoints, four acceptance criteria, and specific coding conventions.

This approach made the development process more organized because the expected behavior was defined before writing the code.

The specification also made testing easier. Each acceptance criterion could be translated into test cases to verify whether the implementation followed the requirements.

I found the structured specification approach more reliable because it reduced misunderstandings and made it easier to identify missing or ambiguous requirements before implementation.

## 5. Testing and Verification Results

The reservation feature was implemented and tested while preserving the existing Library API functionality.

The complete Maven test suite produced the following results:

| Test Group | Tests | Failures | Errors |
|---|---:|---:|---:|
| Existing regression tests | 21 | 0 | 0 |
| Reservation service tests | 18 | 0 | 0 |
| Reservation controller tests | 29 | 0 | 0 |
| **Total** | **68** | **0** | **0** |

The final build completed successfully with `BUILD SUCCESS`.

The tests covered all four acceptance criteria, including reservation creation, unavailable-book rejection, member-specific listing, cancellation, duplicate reservations, missing records, and invalid requests.

The implementation also verified that reservation operations did not modify existing book, member, or borrowing data.

## Conclusion

This lab demonstrated the importance of defining clear software requirements before implementation. Creating a structured specification helped identify expected behavior, clarify business rules, and establish acceptance criteria for testing.

I learned that AI can assist with transforming requirements into working software, but the quality of the result depends heavily on how clearly those requirements are defined and reviewed.

Overall, spec-driven development provided a more organized and reliable approach than relying only on a general prompt.
