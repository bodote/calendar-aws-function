# Requirement 6: Step 3 of Schedule Event Form

## User Story
As a user, I want to proceed from the step-2 event scheduling form to a new step-3 form (`schedule-event-step3`) to finalize the event.

## Acceptance Criteria (for Test Generation)

1. **Navigation to Step 3**
   - Given the user is on the step-2 form page (`schedule-event-step2`),
   - When the user clicks the "next" button,
   - Then the step-3 form page (`schedule-event-step3`) is displayed.

2. **Step 3 Form Layout**
   - The step-3 form contains all the elements visible in the reference screenshot (`../assets/woodle-screenshot-step3.png`).
   - (Note: Only test for the presence of elements, not their style or exact layout.)

3. **Back Navigation from Step 3**
   - Given the user is on the step-3 form page,
   - When the user clicks the "back" button,
   - Then the step-2 form page is displayed,
   - And all previously entered data is preserved and shown.

4. **Expiry Date Default Calculation**
   - Given the user has entered a date on the step-2 form,
   - When the user proceeds to the step-3 form,
   - Then the "Expiry Date" field on step-3 defaults to a date that is exactly 3 months after the date entered on step-2.

5. **Step 3 Form Buttons**
   - The step-3 form contains:
     - A "back" button (as in previous steps).
     - A "create the poll" button.
   - (Note: The summary page shown after "create the poll" will be defined in a future requirement.)

6. **Loading Step 3 with UUID**
   - Given the user accesses the `schedule-event-step3` page with a valid UUID as a path parameter,
   - Then the form is pre-filled with the corresponding event data retrieved from S3.

7. **Redirect on Missing or Invalid UUID**
   - Given the user accesses the `schedule-event-step3` page without a UUID or with an invalid UUID,
   - Then the user is redirected to the `schedule-event` page,
   - And a warning is displayed indicating that the UUID was not found (consistent with the behavior already implemented for step-2). 