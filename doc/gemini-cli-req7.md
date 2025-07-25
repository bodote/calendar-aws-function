# Requirement 7: Event Poll Summary and Persistence

## User Story

As a user who has completed all the steps of creating an event poll, I want to see a summary of all the data I have entered, so that I can review it and get a shareable link to the poll.

## Acceptance Criteria (Test Scenarios)

### Scenario 1: Displaying the Poll Summary

*   **Given** a user has completed steps 1, 2, and 3 of creating a poll.
*   **When** the user clicks the "Create Poll" button on step 3.
*   **Then** the user should be redirected to a summary page.
*   **And** the summary page should display the following information:
    *   Activity Title
    *   Description
    *   Creator's Name
    *   Creator's Email
    *   Event Date
    *   All proposed time slots
    *   The final, shareable event URL.

### Scenario 2: Storing Poll Data in S3

*   **Given** a user has filled out all the necessary information for a poll.
*   **When** the user creates the poll.
*   **Then** all the poll data (title, description, name, email, date, time slots) should be saved as a single object in an S3-compatible object store.
*   **And** the object in S3 should be named with a UUID (e.g., `polls/<UUID>.json`).

### Scenario 3: Generating the Shareable Event URL

*   **Given** a poll has been successfully saved to S3 with a UUID.
*   **When** the summary page is displayed.
*   **Then** a shareable URL should be generated and displayed on the page.
*   **And** the URL should have the format: `http://localhost:8080/event/<UUID>` (where `<UUID>` is the one used for the S3 object).

### Scenario 4: Accessing the Poll via the Shareable URL

*   **Given** a poll has been created and its data is stored in S3.
*   **When** any user (even one without an active session) accesses the shareable URL (`http://localhost:8080/event/<UUID>`).
*   **Then** the application should retrieve the poll data from S3 using the UUID.
*   **And** the summary page for that poll should be displayed with all the correct information.

## Technical Implementation Hints

*   **S3-Compatible Store:** Assume a locally running MinIO instance is available.
*   **S3 Configuration (in `application.yml`):**
    *   **URL:** `http://localhost:9000`
    *   **Bucket Name:** `de.bas.bodo`
*   **Controller:** A new endpoint `GET /event/{uuid}` will be needed in a controller to handle requests for the shareable URL.
*   **Service Layer:** The `PollStorageService` will need to be used to retrieve the data from S3.