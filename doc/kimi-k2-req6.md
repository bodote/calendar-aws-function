# Requirement 6: Event Scheduling Step 3 Form

## User Story
As a user, I want to navigate from step-2 to step-3 form to finalize event creation, so that I can complete the scheduling process.

## Testable Acceptance Criteria

### Navigation Tests
1. **Next Button Navigation Test**: When user clicks "next" on step-2 form, system should redirect to `/schedule-event-step3` with form data preserved
   - Test: Given user is on step-2 form with valid data, when clicking next button, then redirect to step-3 URL
   - Test: Given user navigates to step-3, then previously entered data from step-2 should be pre-populated

### Form Layout Tests
2. **Step-3 Form Layout Test**: Step-3 form should contain required fields and match expected layout
   - Test: Given step-3 page loads, then form contains expiry date field with label "Expiry Date"
   - Test: Given step-3 page loads, then form contains back button labeled "Back"
   - Test: Given step-3 page loads, then form contains create button labeled "Create the poll"
   - Test: Given step-3 page loads, then page structure matches reference screenshot layout

### Data Persistence Tests
3. **Back Button Data Persistence Test**: When user clicks back from step-3, step-2 should show previously entered data
   - Test: Given user is on step-3 with pre-populated data, when clicking back button, then step-2 form shows same data as before

### Default Value Tests
4. **Expiry Date Default Calculation Test**: Expiry date should default to step-2 date + 3 months
   - Test: Given step-2 has date "2024-01-15", when navigating to step-3, then expiry date shows "2024-04-15"
   - Test: Given step-2 has date "2024-12-31", when navigating to step-3, then expiry date shows "2025-03-31"

### UUID Handling Tests
5. **UUID Parameter Loading Test**: When step-3 is called with UUID parameter, corresponding S3 data should load
   - Test: Given valid UUID in path `/schedule-event-step3/{uuid}`, then form loads data from S3 storage
   - Test: Given invalid/missing UUID, then redirect to `/schedule-event` with warning message "UUID was not found"
   - Test: Given no UUID parameter, then redirect to `/schedule-event` with warning message "UUID was not found"

### Button Functionality Tests
6. **Create Button Navigation Test**: When user clicks "Create the poll" button, system should navigate to summary page
   - Test: Given user is on step-3 with valid data, when clicking create button, then navigate to summary page (implementation in future requirement)

## Technical Notes for Implementation
- Use existing S3 storage service for data persistence
- Follow existing pattern for form data handling between steps
- Maintain hexagonal architecture with domain logic separate from infrastructure
- Use JTE templates for view layer
- Implement proper validation before navigation 