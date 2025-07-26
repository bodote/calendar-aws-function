# Requirement 6
As a user, I want to be able to proceed from the step-2 page to the next form page `schedule-event-step3` to finalize the event.
![Schedule Event Form 3](../assets/woodle-screenshot-step3.png)

## Acceptance Criteria
Each criterion is to be addressed one by one using TDD: write a failing test first (using JGiven for readability), get user approval, then implement the minimal code to make it pass.

### Functional Criteria (Use strict TDD)
1. **Navigation from Step 2 to Step 3**  
   Given the user is on the step-2 form with valid data entered,  
   When the user clicks the "Next" button,  
   Then the step-3 form appears, pre-filled with the data from previous steps.

2. **Back Navigation from Step 3 to Step 2**  
   Given the user is on the step-3 form with data entered in previous steps,  
   When the user clicks the "Back" button,  
   Then the step-2 form appears again, pre-filled with the previously entered data.

3. **Default Expiry Date Calculation**  
   Given the user has entered dates in step 2,  
   When the step-3 form is loaded,  
   Then the "Expiry Date" field defaults to the latest date from step 2 plus 3 months.

4. **Retrieve Poll Data with UUID**  
   Given a valid UUID is provided as a path parameter to `schedule-event-step3`,  
   When the page is loaded,  
   Then the corresponding poll data is retrieved from S3 and displayed in the form.

5. **Redirect if No UUID Provided**  
   Given no UUID is provided as a path parameter to `schedule-event-step3`,  
   When the page is loaded,  
   Then the user is redirected to the `schedule-event` page with a warning that the UUID was not found (similar to step-2 implementation).

### UI Criteria (Do not use TDD for layout; test presence of elements only)
6. The layout of the step-3 form should look similar to ![Schedule Event Form 3](../assets/woodle-screenshot-step3.png).  
   - Test that required elements (e.g., Expiry Date field, Back button, Create Poll button) are present, but do not test styling, positioning, or image details.

7. The step-3 form includes a "Back" button and a "Create the Poll" button.  
   - The "Create the Poll" button will lead to a summary page (to be defined in a future requirement).  
   - Test presence of these buttons, but implement functionality in later requirements. 