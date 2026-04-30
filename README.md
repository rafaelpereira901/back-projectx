# ProjectX API

Spring Boot backend for authentication, author/book catalog management, and user profile management.

## What This Project Provides

- JWT-based authentication (`signup`, `login`)
- CRUD for authors
- CRUD for books
- Public profile read, authenticated profile update/delete
- Actuator endpoints for health and metrics
- OpenAPI 3 documentation and Swagger UI
- Split application logging (console + info file + error file)

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Security
- Spring Data JPA
- H2 database (file mode)
- JJWT (`io.jsonwebtoken`)
- Maven

## Project Structure

- `src/main/java/com/agoracorp/projectx/controller`: REST controllers
- `src/main/java/com/agoracorp/projectx/service`: business logic
- `src/main/java/com/agoracorp/projectx/repository`: persistence layer
- `src/main/java/com/agoracorp/projectx/security`: JWT/security config and filter
- `src/main/java/com/agoracorp/projectx/dto`: request/response contracts
- `src/main/resources`: profiles and logging config
- `src/test/java/com/agoracorp/projectx`: unit and context tests
- `https`: ready-to-use HTTP request files for manual API testing

## Prerequisites

- JDK 21+
- Maven (`mvn`) installed

Note: `mvnw` exists in the repository, but `.mvn/wrapper` is missing. Use system Maven commands unless wrapper files are added.

## Quick Start

### 1) Clone and open

```bash
git clone <your-repo-url>
cd back-shelfspace
```

### 2) Set environment variables

Linux/macOS:

```bash
export SPRING_PROFILES_ACTIVE=dev
export APP_JWT_SECRET='replace-this-with-a-very-long-random-secret-key-at-least-32-bytes'
```

Windows (PowerShell):

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:APP_JWT_SECRET="replace-this-with-a-very-long-random-secret-key-at-least-32-bytes"
```

### 3) Run the API

```bash
mvn spring-boot:run
```

App default URL: `http://localhost:8080`

## Run and Debug in VS Code

This repository already includes launch profiles in `.vscode/launch.json`:

- `Run Projectx (dev)`
- `Run Projectx (prod)`

Use Run and Debug panel, then select the profile and start.

## Profiles and Configuration

- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

No base `application.properties` is used. Choose a profile explicitly (`dev` or `prod`).

### Important Environment Variables

- `SPRING_PROFILES_ACTIVE`: `dev` or `prod`
- `SPRING_DATASOURCE_URL`: required in `prod` profile
- `SPRING_DATASOURCE_USERNAME`: required in `prod` profile
- `SPRING_DATASOURCE_PASSWORD`: required in `prod` profile
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME`: defaults to `org.postgresql.Driver`
- `APP_JWT_SECRET`: JWT signing secret
- `APP_JWT_EXPIRATION_MINUTES`: token expiration (default `60`)
- `APP_SECURITY_CORS_ALLOWED_ORIGIN_PATTERNS`: CORS origin patterns
- `APP_INFO_LOG_FILE`: info log output path
- `APP_ERROR_LOG_FILE`: error log output path

## Database

Database profile behavior:

- Dev profile: H2 file database (`jdbc:h2:file:./data/projectxdb;MODE=PostgreSQL;`)
- Prod profile: datasource is provided by environment variables (`SPRING_DATASOURCE_*`), intended for PostgreSQL

Local DB file is created under `data/`.

## Security Model

Current HTTP security behavior:

- Public access rules:
- `GET /**`
- `OPTIONS /**`
- `/auth/**` (signup/login)
- `/h2-console/**` only when H2 console is enabled (dev)

- Authenticated access rule:
- Any non-GET endpoint outside `/auth/**`

Authentication type:

- Stateless JWT
- Form login disabled
- HTTP Basic disabled

## API Endpoints

### Auth

- `POST /auth/signup`
- `POST /auth/login`

### Authors

- `GET /authors`
- `GET /authors/{id}`
- `POST /authors`
- `PUT /authors/{id}`
- `DELETE /authors/{id}`

### Books

- `GET /books`
- `GET /books/{id}`
- `POST /books`
- `PUT /books/{id}`
- `DELETE /books/{id}`

### Profile

- `GET /profiles/{profileId}` (public)
- `PUT /profiles/{profileId}` (JWT required, owner-only in service)
- `DELETE /profiles/{profileId}` (JWT required, owner-only in service)

### Reading History

- `POST /profiles/{profileId}/books/{bookId}/reading-histories` (JWT required, owner-only)
- `GET /profiles/{profileId}/reading-histories` (public)
- `GET /profiles/{profileId}/books/{bookId}/reading-histories` (public)
- `GET /books/{bookId}/reading-histories` (public)
- `GET /books/{bookId}/reading-histories/latest` (public)

### Reviews

- `PUT /profiles/{profileId}/books/{bookId}/review` (JWT required, owner-only)
- `DELETE /profiles/{profileId}/books/{bookId}/review` (JWT required, owner-only)
- `GET /profiles/{profileId}/reviews` (public)
- `GET /profiles/{profileId}/books/{bookId}/review` (public)
- `GET /books/{bookId}/reviews` (public)

## Manual API Testing

Use the request files in `https/`:

- `https/auth.http`
- `https/author.http`
- `https/book.http`
- `https/profile.http`
- `https/reading-history.http`
- `https/review.http`
- `https/shelf.http`

These can be executed from VS Code REST Client style workflows.

## API Documentation (OpenAPI 3)

Springdoc OpenAPI is enabled for this project.

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

To authorize secured endpoints in Swagger UI:

1. Click **Authorize**.
2. Paste your JWT token in bearer format.
3. Execute non-GET endpoints directly from the UI.

## Observability (Actuator)

Exposed endpoints include:

- `health`
- `info`
- `metrics`
- `prometheus`

Base path: `/actuator`

Example:

- `GET /actuator/health`

## Logging

Logging is configured in `src/main/resources/logback-spring.xml` with three outputs:

- Console appender
- Info file appender (INFO/WARN)
- Error file appender (ERROR only)

Default file locations:

- Dev info: `logs/projectx-dev-info.log`
- Dev error: `logs/projectx-dev-error.log`
- Prod info: `logs/projectx-info.log`
- Prod error: `logs/projectx-error.log`

All appenders use the same pattern:

```text
%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-5level [%thread] %logger{36} - %msg%n
```

## Testing

Run all tests:

```bash
mvn test
```

Run a specific test class:

```bash
mvn -Dtest=ProjectxApplicationTests test
```

Current suite includes service, controller, model validation, JWT, and context-load coverage.

## Git Notes

- `*.properties` is ignored in `.gitignore`, except:
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`
- Local DB and logs are ignored.
- If a previously tracked file keeps appearing in changes, remove it from index once:

```bash
git rm --cached <file>
```

## Onboarding Checklist

1. Install JDK 21 and Maven.
2. Set `SPRING_PROFILES_ACTIVE` and `APP_JWT_SECRET`.
3. For `prod`, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.
4. Start app with `mvn spring-boot:run` or VS Code launch profile.
5. Verify `GET /actuator/health`.
6. Use `https/auth.http` to create/login user.
7. Use returned token for write endpoints.
8. Run `mvn test` before opening PRs.
