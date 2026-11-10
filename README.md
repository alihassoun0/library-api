# library-api

A Spring Boot 3 REST API for Labs 1–2, using Java 17, Maven, Spring Web,
Spring Data JPA, Validation, H2, and Springdoc OpenAPI.

## Run

Install JDK 17 and Maven 3.6.3 or newer. Set `JAVA_HOME` to the JDK 17 directory
and add its `bin` directory to `PATH`. Check the Java runtime Maven uses:

```shell
java -version
mvn -version
```

From the project directory, compile and package the application:

```shell
mvn clean package
```

Run it with Maven:

```shell
mvn spring-boot:run
```

Alternatively, run the packaged application:

```shell
java -jar target/library-api-0.0.1-SNAPSHOT.jar
```

The application listens at `http://localhost:8080`. Stop it with Ctrl+C.
If port 8080 is occupied, stop the other application or run the JAR with
`--server.port=8081` and use that port in the URLs below.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/books` | List all books |
| GET | `/books/{id}` | Get one book |
| POST | `/books` | Create a book |
| PUT | `/books/{id}` | Replace an existing book's fields |
| DELETE | `/books/{id}` | Delete a book |
| GET | `/members` | List all members |
| GET | `/members/{id}` | Get one member |
| POST | `/members` | Create a member |
| POST | `/borrowings` | Borrow an available book for 14 days |
| PUT | `/borrowings/{id}/return` | Record a return and its fine |
| GET | `/members/{id}/borrowings` | List a member's active borrowings |

GET and PUT return HTTP 200. POST returns HTTP 201 with a `Location` header.
DELETE returns HTTP 204. Requests for missing records return HTTP 404.
POST generates the ID; any ID supplied in the body is ignored.

Send JSON bodies with `Content-Type: application/json`. For POST `/books`
or PUT `/books/{id}`:

```json
{
  "isbn": "9780132350884",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "genre": "Programming",
  "publishedYear": 2008,
  "available": true
}
```

For POST `/members`:

```json
{
  "name": "Sara Nasser",
  "email": "sara@example.com",
  "membershipDate": "2026-11-09",
  "active": true
}
```

Dates use `YYYY-MM-DD`. Supply all fields when replacing a book with PUT.
The entities intentionally have no extra validation or uniqueness constraints.
Borrowing requests accept `{"bookId": 1, "memberId": 1}`. Borrowing returns
HTTP 201 and makes the book unavailable. Returning a book returns HTTP 200
with the updated borrowing and restores book availability. The fine is
EUR 0.50 per overdue day, or zero for a return on or before the due date.
Unavailable books and repeated returns produce HTTP 409; missing records
produce HTTP 404. Active borrowings have a null `returnDate`.

Run the complete test suite with `mvn test`. Lab 2's review, command evidence,
and completion status are recorded in `docs/lab2-xp-review.md`,
`docs/lab2-evidence.md`, and `reports/lab2-report.md`. Personal hours and
reflections require student input before the lab can be marked complete.

## Swagger UI

Open <http://localhost:8080/swagger-ui/index.html> to inspect and try the endpoints.
The OpenAPI JSON document is at <http://localhost:8080/v3/api-docs>.

## H2 console and sample data

Open <http://localhost:8080/h2-console> and connect using:

- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:librarydb`
- User Name: `sa`
- Password: leave blank

Hibernate creates the tables before `data.sql` inserts five books and three members.
The database lives in memory and resets on each application restart.

Check the data in the console:

```sql
SELECT * FROM books;
SELECT * FROM members;
SELECT * FROM borrowings;
```

You can also open <http://localhost:8080/books> and
<http://localhost:8080/members> to see the sample data as JSON.
