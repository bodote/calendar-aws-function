# Requirement 6 – Navigate to Step-3 of the “Schedule Event” wizard

## User Story
As a meeting organiser I want to proceed from the second step of the “Schedule Event” wizard to the third step where I can enter final information, so that I can create the poll.

## Domain Language
- **Step-2 page**: `/schedule-event-step2/{uuid}`
- **Step-3 page**: `/schedule-event-step3/{uuid}`
- **UUID**: Poll identifier (RFC 4122)

## Acceptance Criteria (each MUST be implemented with its own failing test first)

1. **Happy-path navigation**
   - **Given** I am on `/schedule-event-step2/{uuid}` for an existing poll
   - **When** I press the **Next** button
   - **Then** I am redirected (HTTP 302) to `/schedule-event-step3/{uuid}` and receive the Step-3 HTML with status **200**.

2. **Step-3 form layout**
   - **Given** I request `/schedule-event-step3/{uuid}`
   - **Then** the response contains the Step-3 HTML form (see `woodle-screenshot-step3.png`) with _at least_ the following elements:
     1. An input field named `expiryDate` (type `date`).
     2. A **Back** button.
     3. A **Create the poll** submit button.

3. **Back navigation from Step-3**
   - **Given** I am on `/schedule-event-step3/{uuid}` with previously entered data
   - **When** I click the **Back** button
   - **Then** I am returned to `/schedule-event-step2/{uuid}`
   - **And** all fields on Step-2 are pre-filled with the data I had already entered.

4. **Default expiry-date calculation**
   - **Given** the event start date selected on Step-2 is `yyyy-MM-dd`
   - **When** I open `/schedule-event-step3/{uuid}`
   - **Then** the `expiryDate` field defaults to **start date + 3 calendar months** (same day number, or last valid day if the month is shorter).

5. **Direct access with existing UUID**
   - **Given** a poll with id `{uuid}` exists in persistent storage
   - **When** I `GET /schedule-event-step3/{uuid}`
   - **Then** the response status is **200** and the Step-3 form is pre-populated with that poll’s data.

6. **Direct access with missing or unknown UUID**
   - **Given** no poll exists with id `{uuid}`
   - **When** I `GET /schedule-event-step3/{uuid}`
   - **Then** I am redirected to `/schedule-event` (HTTP 302)
   - **And** the page displays the warning message **“Poll not found”**.

7. **Direct access without UUID**
   - **When** I `GET /schedule-event-step3` (no path parameter)
   - **Then** I am redirected to `/schedule-event` (HTTP 302)
   - **And** the page displays the warning message **“Poll id missing”**.

---

### Notes for Test Implementation
- if 2 or more tests have the same **Given** and **When** part, merge them in to one test using `then()....and()....and()`
- Follow strict **Test-Driven Development**: write each test first, ensure it fails, then implement the minimum production code to pass it.
- Use **JGiven** for readable BDD-style tests.
- Prefer **@WebMvcTest** with **MockMvc** for controller-level behaviour; mock out persistence.
- Verify routing, HTTP status codes, template names, model attributes, and presence of required HTML form elements – do **not** test CSS, fonts, or graphical layout.
- Maintain hexagonal architecture: domain logic in core, storage and web in adapters. 