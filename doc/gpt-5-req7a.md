## Requirement 7a: E2E happy-path test for creating a scheduled event

As a developer, I want an automated end-to-end (E2E) test that covers the happy path of creating a new scheduled event through the browser UI so that I can verify the full flow works.

### Scope
- Cover only the happy path via the UI: fill the form on `/schedule-event`, proceed through all steps, and verify the final event summary page.
- No assertions on layout or styles; only content/element presence and values.

### Constraints and approach
- Use Java Playwright bindings inside a JUnit 5 test class.
- Start the Spring Boot app in-process on a random port using `@SpringBootTest(webEnvironment = RANDOM_PORT)` with profile `e2e` (or `test`).
- Prefer an in-memory storage adapter for E2E (selectable by the profile) to avoid LocalStack dependency. If LocalStack is used, the test must clean up created S3 objects.

### Test environment
- Java 21, Maven, headless browser.
- Playwright should auto-download browsers on first run.

### Selectors and routes
- Start page: GET `/schedule-event`
  - Inputs (by data-test attrs):
    - `data-test-your-name-field`
    - `data-test-email-field`
    - `data-test-activity-title-field`
    - `data-test-description-field`
  - Next button: `data-test-next-button`
- Final page: GET `/event/{uuid}` (after completing steps)
  - Must render the previously entered values in the summary.

### Test data
- yourName: `Alice Agent`
- emailAddress: `alice@example.com`
- activityTitle: `Planning Session`
- description: `Discuss Q3 milestones`

### Acceptance criteria
- Navigates to `/schedule-event` on the random test port.
- Fills the four fields with the test data and clicks Next.
- Completes remaining steps with minimal required inputs and follows redirects to the summary.
- Lands on `/event/{uuid}` where `{uuid}` is non-empty.
- Summary page contains all four values entered before.
- All HTTP responses are 200 during the flow.

### Implementation outline (for the agent)
- Add test-scope dependency `com.microsoft.playwright:playwright` to `pom.xml`.
- Create `E2ECreateEventTest` under `src/test/java/...` annotated with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@Tag("e2e")`.
- Inject `@LocalServerPort` and build `baseUrl`.
- In `@BeforeAll`, start Playwright and a headless Chromium `Browser`; close in `@AfterAll`.
- In the test: open `baseUrl + "/schedule-event"`, fill fields via data-test attributes, click Next, proceed to the final page, assert the summary shows the inputs.
- Add a Maven profile `e2e` that only runs tests tagged `e2e`.

### Commands
- Run locally: `mvn -Pe2e test`

### Cleanup
- If using LocalStack: delete any created S3 objects in `@AfterEach` to keep the bucket clean.