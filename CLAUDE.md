# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is "Woodle" - a calendar/polling web application built with Spring Boot 3 and designed for AWS Lambda deployment. The application allows users to create time slot polls for scheduling events through a multi-step form wizard.

**Key Technologies:**
- Spring Boot 3.5.3 with Java 21
- JTE templating engine for server-side rendering
- AWS Serverless Java Container for Lambda deployment
- MinIO/S3 for poll data storage
- JGiven for BDD-style testing
- Maven for build management

## Architecture

**Multi-Step Form Flow:**
1. **Step 1** (`/schedule-event`): Event details (name, email, activity title, description)
2. **Step 2** (`/schedule-event-step2/{uuid}`): Date and time slots selection
3. **Step 3** (`/schedule-event-step3/{uuid}`): Expiry date configuration

**Storage Layer:**
- `PollStorageService` interface with two implementations:
  - `InMemoryPollStorageService` for development/testing
  - `S3PollStorageService` for production (MinIO/S3 compatible)

**Controller Layer:**
- `WoodleFormsController` handles all form interactions and navigation
- Uses UUID-based routing for poll persistence across steps

**Template Layer:**
- JTE templates in `src/main/jte/` for server-side HTML rendering
- Uses `data-test` attributes for stable test element selection

## Common Development Commands

**Build and Test:**
```bash
# Build the project
./mvnw clean compile

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=WoodleFormsTest

# Package for Lambda deployment
./mvnw package

# Generate JGiven test reports
./mvnw jgiven:report
```

**Local Development:**
```bash
# Run Spring Boot application locally
./mvnw spring-boot:run

# Start MinIO for local S3-compatible storage
doc/minio-start.sh

# Stop MinIO
doc/minio-stop.sh

# Check MinIO status and cleanup
doc/minio-check.sh
doc/minio-cleanup.sh
```

**AWS Deployment:**
```bash
# Deploy to AWS Lambda (requires SAM CLI)
sam build
sam deploy
```

## Testing Guidelines

**Test Types:**
- Use `@WebMvcTest` over `@SpringBootTest` for faster execution
- Add `@ImportAutoConfiguration(JteAutoConfiguration.class)` for JTE template tests
- Use JGiven BDD framework for readable test scenarios
- Use Jsoup for HTML parsing and element verification

**Test Structure (from .cursor rules):**
- `given()` methods set preconditions
- `when()` methods perform the actual action being tested
- `then()` methods verify results
- Split MockMvc calls between `when()` (action) and `then()` (verification) stages

**HTML Testing:**
- Use `data-test` attributes for stable element selection
- Use Jsoup selectors like `doc.select("div[data-test='event-details']")`
- Prefer HTTP status `303` for POST-redirect-GET patterns

## Development Configuration

**Profiles and Properties:**
- Main config: `src/main/resources/application.properties`
- Test overrides: Use `@TestPropertySource` or `application-test.yml`
- JTE development mode enabled for template hot-reloading

**Debugging:**
- Enable DEBUG logging in application.yml for Spring Boot path resolution issues
- Use Spring Boot debug options for 404 troubleshooting