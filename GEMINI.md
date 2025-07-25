# Gemini Rules for this Project

This document outlines the rules and conventions for Gemini to follow when assisting with this project.

## General

*   The primary language is Java 21 with Spring Boot.
*   The architecture is Hexagonal (Ports and Adapters). Maintain this separation of concerns.
*   New features require corresponding tests.

## Testing

*   **JGiven:**
    *   Use JGiven for behavior-driven development (BDD).
    *   `given()` methods should establish preconditions.
    *   `when()` methods should perform the action under test.
    *   `then()` methods should verify the outcome.
    *   Method bodies must reflect their names. An empty `when()` method is a sign of a problem.
*   **Spring Tests:**
    *   Prefer `@WebMvcTest` over `@SpringBootTest` for faster tests.
    *   When using `@WebMvcTest` with JTE templates, include `@ImportAutoConfiguration(JteAutoConfiguration.class)`.
    *   Use `MockMvc` and `Jsoup` for testing HTML content.
    *   Use `data-test-*` attributes in HTML to create stable test selectors.
    *   Avoid code duplication in tests.
    *   Use `application.yml` for configuration. For tests, use a `test` profile with `application-test.yml` or `@TestPropertySource`.
    *   Use status code `303` for redirects after a POST request.
*   **Debugging:**
    *   When tests fail, enable `DEBUG` logging in `application.yml` or `application-test.yml` to get more detailed output.
    *   Use Spring Boot's debugging options to trace request routing issues (e.g., for 404 errors).

## JTE Templates

*   Use the `for-each` style for loops.
*   Do not use the `@{ ... }` syntax; it is not supported. Refer to the JTE documentation for correct variable syntax.
