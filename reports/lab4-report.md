# Lab 4 – Design, Implementation and Deployment

**Project:** Library Management API  
**Course:** Software Engineering (2026–2027)  
**AI Development Tool:** OpenAI Codex CLI

## 1. Design Review

The objective of this lab was to review the existing application architecture and improve its design, maintainability, and deployment process.

The initial review identified several problems:

| Identified Problem | Severity | Proposed Improvement |
|---|---|---|
| Controllers handled database operations directly. | Critical | Introduce service classes to separate business logic from HTTP handling. |
| Controllers returned JPA entities directly. | Critical | Introduce DTOs and mappers to separate the API structure from database entities. |
| Exception handling was inconsistent across controllers. | Important | Create a global exception handler. |
| API documentation lacked detailed descriptions and response information. | Important | Add OpenAPI annotations to controllers and DTOs. |
| Some tests contained repeated setup code. | Minor | Keep the current tests and improve their organization in later refactoring work. |

The two most important problems were the direct use of repositories inside controllers and the exposure of JPA entities through REST endpoints.

To improve the architecture, BookService and MemberService were introduced. Controllers now handle HTTP requests and delegate application operations to services.

DTOs and mappers were also added to control the data exchanged between the application and its clients.

## 2. DTOs and Their Importance

DTO stands for Data Transfer Object. A DTO is used to transfer data between the application and its clients without exposing the internal database entities directly.

Previously, the controllers returned JPA entities as JSON responses. This created a dependency between the database structure and the public API.

For example, BookDTO contains only the fields required by the API:

- id
- isbn
- title
- author
- genre
- publishedYear
- available

BookMapper converts between the Book entity and BookDTO.

The same approach was applied to Member, Borrowing, and Reservation.

Using DTOs provides several advantages:

1. **Better security:** Internal entity fields are not automatically exposed to API users.
2. **Maintainability:** Database changes do not automatically change the API response structure.
3. **Separation of concerns:** Entities represent database information, while DTOs represent data exchanged through the API.
4. **Better control:** Developers decide which fields clients can send and receive.

The existing JSON structure and tested API behavior were preserved during this change.

## 3. Design Patterns Identified

Several software design patterns are used in the Library Management API.

### Repository Pattern

The repository pattern separates database operations from the business logic.

For example, BookRepository and ReservationRepository extend JpaRepository and provide methods for retrieving, saving, and deleting records.

This makes database access easier to organize and maintain.

### Service Layer Pattern

The service layer contains application operations and business logic.

For example, BookService handles book-related operations, while BorrowingService manages borrowing and returning books.

Controllers call these services instead of accessing repositories directly. This improves separation of concerns and reduces coupling.

### DTO and Mapper Pattern

DTOs define the information exchanged through the API, while mappers convert between DTOs and database entities.

For example, BookMapper converts Book entities into BookDTO objects.

### Global Exception Handling

GlobalExceptionHandler uses Spring's @ControllerAdvice to handle exceptions in one place.

It provides consistent HTTP error responses for different situations, including:

- HTTP 400 – Invalid requests.
- HTTP 404 – Missing resources.
- HTTP 409 – Business conflicts.
- HTTP 500 – Unexpected server errors.

The error responses include an error message and timestamp.

These patterns make the application easier to understand, maintain, and extend.

## 4. Deployment Using Docker

Docker was used to package the Library Management API into a container so that the application could run in a consistent environment.

A multi-stage Dockerfile was created.

The first stage uses Maven and Java 17 to build the application. The second stage uses a lightweight Java runtime image to execute the generated JAR file.

The Dockerfile uses the following images:

- Build stage: `maven:3.9-eclipse-temurin-17`
- Runtime stage: `eclipse-temurin:17-jre-alpine`

The application exposes port 8080 and uses the following JVM options:

`-Xmx256m -Xms128m`

A `.dockerignore` file was also created to exclude unnecessary files from the Docker build context.

### Docker commands

The image was built using:

```bash
docker build -t library-api:lab4 .
```

The container was started using:

```bash
docker run -p 8080:8080 library-api:lab4
```

The application was then tested using:

```bash
curl.exe http://localhost:8080/books
```

The request returned HTTP 200 with the five sample books, confirming that the application was running successfully inside the container.

The Swagger UI was also opened and verified. It displayed 14 documented API operations across four controller groups.

During deployment, Docker initially failed because the Docker Desktop Linux engine was not running. Starting Docker Desktop resolved the problem, and the image was built and executed successfully.

The application currently uses an in-memory H2 database. Moving to PostgreSQL would require additional database configuration, a suitable persistence strategy, and deployment changes. PostgreSQL was not part of this lab's completed implementation.

## 5. Testing and Verification

The complete Maven test suite was executed after the design and implementation changes.

| Test Stage | Tests | Failures | Errors |
|---|---:|---:|---:|
| Before implementation | 68 | 0 | 0 |
| After DTO and architecture changes | 68 | 0 | 0 |
| Final test suite | **81** | **0** | **0** |

The final build completed successfully with `BUILD SUCCESS`.

The tests verified the DTO structure, mapper conversions, global exception handling, and existing application functionality.

All previous 68 tests remained passing, confirming that the architectural improvements did not break the existing borrowing and reservation features.

## Conclusion

This lab demonstrated the importance of good software architecture and proper separation of concerns.

I learned that using DTOs helps separate the database structure from the public API, while service classes and repositories make the application easier to maintain.

The global exception handler also provides more consistent error responses, and OpenAPI improves API documentation.

Finally, Docker made it possible to package and run the application in a separate environment. Overall, these improvements made the Library Management API more organized, maintainable, and easier to deploy.
