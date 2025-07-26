# Requirement 6: Schedule Event Step 3 Form

## User Story 
As a user, I want to navigate from the step-2 page to a step-3 form page (`schedule-event-step3`) to finalize the event configuration.

## Reference UI
![Schedule Event Form 3](../assets/woodle-screenshot-step3.png)

## Technical Requirements

### HTTP Endpoints to Implement
- `GET /schedule-event-step3/{uuid}` - Load step 3 with existing poll data
- `GET /schedule-event-step3` - Redirect to step 1 with error message
- `POST /schedule-event-step3/{uuid}` - Submit step 3 form data

### Form Fields Required
- **Expiry Date**: Date input field with default calculated value
- **Back Button**: Navigation to previous step
- **Create Poll Button**: Submit form to create poll

## Acceptance Criteria (for TDD)

### AC1: Navigation from Step 2 to Step 3
**Given** a user is on the step-2 form (`schedule-event-step2`)  
**When** the user clicks the "Next" button  
**Then** the system should:
- Submit the step-2 form data via POST
- Navigate to `/schedule-event-step3/{uuid}` 
- Display the step-3 form

**Test Focus:**
- Verify POST request to step-2 endpoint includes form data
- Verify redirect to step-3 URL with correct UUID
- Verify step-3 page loads successfully

### AC2: Step 3 Form Layout and Structure
**Given** a user navigates to `/schedule-event-step3/{uuid}`  
**When** the page loads  
**Then** the form should contain:
- An "Expiry Date" input field
- A "Back" button
- A "Create the poll" button
- The same header/footer layout as previous steps

**Test Focus:**
- Verify presence of required form elements
- Verify form has correct action and method attributes
- Verify CSS classes and styling structure

### AC3: Expiry Date Default Calculation
**Given** a user loads step-3 with poll data from step-2  
**When** the page renders  
**Then** the "Expiry Date" field should:
- Display a default date that is exactly 3 months after the event date from step-2
- Be editable by the user
- Use proper date format (yyyy-MM-dd)

**Test Focus:**
- Unit test for date calculation logic
- Integration test verifying correct default value display
- Test edge cases (month boundaries, leap years)

### AC4: Back Button Navigation with Data Preservation
**Given** a user is on step-3 form  
**When** the user clicks the "Back" button  
**Then** the system should:
- Navigate back to `/schedule-event-step2/{uuid}`
- Display step-2 form with all previously entered data intact
- Maintain the same UUID throughout navigation

**Test Focus:**
- Verify back navigation preserves form data
- Verify URL parameter handling
- Test data persistence between form steps

### AC5: Form Submission Behavior
**Given** a user fills out the step-3 form  
**When** the user clicks "Create the poll" button  
**Then** the system should:
- Validate all form data
- Store the complete poll configuration
- Navigate to a summary page (implementation details in future requirement)

**Test Focus:**
- Verify form validation
- Verify data storage/persistence
- Verify proper response handling

### AC6: UUID Parameter Validation - Valid UUID
**Given** a user navigates to `/schedule-event-step3/{uuid}` with a valid UUID  
**When** the page loads  
**Then** the system should:
- Retrieve poll data from S3 storage using the UUID
- Pre-populate form fields with retrieved data
- Display the step-3 form successfully

**Test Focus:**
- Mock S3 service integration
- Test UUID parameter extraction
- Verify data retrieval and form population

### AC7: UUID Parameter Validation - Invalid/Missing UUID
**Given** a user navigates to `/schedule-event-step3` without UUID or with invalid UUID  
**When** the page loads  
**Then** the system should:
- Redirect to `/schedule-event` (step-1)
- Display warning message: "Poll not found or UUID is invalid"
- Clear any existing form data

**Test Focus:**
- Test missing UUID parameter handling
- Test invalid UUID format handling
- Verify error message display
- Verify redirect behavior

## Implementation Order for TDD

1. **Start with Controller Tests**: Test URL mapping and basic navigation
2. **Form Structure Tests**: Verify HTML form elements and structure
3. **Data Flow Tests**: Test data persistence between steps
4. **Date Calculation Tests**: Unit tests for expiry date logic
5. **Integration Tests**: End-to-end form flow testing
6. **Error Handling Tests**: UUID validation and error scenarios

## Data Models Expected

```java
// Expected form data structure for step 3
public record ScheduleEventStep3Form(
    LocalDate expiryDate,
    // Additional fields may be added based on implementation needs
) {}
```

## Dependencies
- Requires step-2 implementation to be complete
- Requires S3 storage service for poll data persistence
- Requires UUID-based poll identification system 