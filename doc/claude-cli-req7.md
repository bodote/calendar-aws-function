# Requirement 7: Event Summary Page with Persistent Storage

**Context**: This requirement builds on req1-req6, which established the 3-step event creation wizard:
- req1: Basic "woodle" homepage with JTE templates  
- req2: Homepage with "Schedule Event" button and initial form
- req3-req5: Multi-step form implementation with UUID-based navigation
- req6: Step 3 completion with "create the poll" button

## User Story
As a user who has completed all 3 steps of event creation (from req6), when I click "create the poll" button, I want to see a comprehensive summary of all my entered data and have the event persistently stored so I can share it with others via a permanent URL.

## TDD Implementation Approach
**CRITICAL**: Follow strict Test-Driven Development - write failing tests FIRST, then implement just enough code to make them pass.

### Test-First Development Steps:

1. **FIRST**: Write failing test for `/event/{uuid}` GET endpoint
2. **THEN**: Implement minimal controller method to make test pass
3. **FIRST**: Write failing test for S3 data persistence after step 3 completion  
4. **THEN**: Implement S3 storage integration
5. **FIRST**: Write failing test for template rendering with all form data
6. **THEN**: Create and populate summary template
7. **FIRST**: Write failing test for session-independent access
8. **THEN**: Implement data retrieval from S3 without session dependency

## Acceptance Criteria

### 1. Summary Display (Test First!)
- Create test that verifies summary page shows data from all 3 steps:
  - Step 1: User name, email, activity title, description
  - Step 2: Selected dates and time slots  
  - Step 3: Expiry date
- Layout should match ![Event Summary page](../assets/woodle-screenshot-summary.png)
- Template: `src/main/jte/event-summary.jte`

### 2. Data Persistence (Test First!)
- Write test that verifies data is stored in S3-compatible store after step 3 completion
- Object key must use the existing UUID from the form wizard
- Data format: Store complete `PollData` object as JSON
- Use existing `S3PollStorageService` from previous requirements

### 3. URL Generation (Test First!)
- Test that summary page displays shareable event URL
- URL pattern: `http://localhost:8080/event/{uuid}`
- Example: `http://localhost:8080/event/123e4567-e89b-12d3-a456-426614174000`

### 4. Session-Independent Access (Test First!)
- Write test that `/event/{uuid}` works without HTTP session
- Test data retrieval directly from S3 using UUID from URL path
- Test 404 response when UUID not found in storage

## Required Configuration
Add to `src/main/resources/application.yml`:
```yaml
minio:
  endpoint: http://localhost:9000
  bucket-name: de.bas.bodo
```

## Testing Strategy
- Use `@WebMvcTest(WoodleFormsController.class)` with `@ImportAutoConfiguration(JteAutoConfiguration.class)`
- Use `InMemoryPollStorageService` for test isolation
- Use JGiven BDD format for readable test scenarios
- Test with existing UUID patterns from req3-req6 implementation

## Integration Points
- Extend existing `WoodleFormsController` from previous requirements
- Use existing `PollStorageService` interface and S3 implementation
- Build on existing UUID-based navigation from req3-req6
- Integrate with existing JTE template structure from req1-req2
  