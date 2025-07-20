# Requirement 6 – Navigate to “Schedule Event – Step 3”

## Feature Overview
A visitor who is currently creating an event poll must be able to proceed from the **step-2** form to a **step-3** form where the expiry date of the poll can be reviewed/edited and the poll can finally be created.

## Technical Context
Application stack: Java 17, Spring Boot, Hexagonal Architecture, HTML rendered via JTE templates, human-readable tests written with **JGiven**.

## Functional Acceptance Criteria (expressed as Given/When/Then)

### AC-1  – Forward Navigation
* **Given** the visitor is on URL `/schedule-event-step2` and has entered valid data
* **When** they click the button labelled **Next**
* **Then** the response is HTTP 200 and the JTE template `schedule-event-step3.jte` is rendered
* **And** all data already entered in steps 1 & 2 is present in the rendered form fields

### AC-2 – Basic Layout / Elements
(To be verified only by asserting presence of elements, not CSS details)
* **Given** the visitor is on the rendered step-3 page
* **Then** the page contains the following form controls:
  * an input field named `expiryDate`
  * a **Back** button
  * a **Create Poll** button

### AC-3 – Back Navigation
* **Given** the visitor is on `/schedule-event-step3`
* **When** they press **Back**
* **Then** the application returns to `/schedule-event-step2` **with all previously entered data pre-populated**

### AC-4 – Default Expiry Date Calculation
* **Given** the visitor selected a reference date **D** on step 2
* **When** step-3 is rendered for the first time in the current session
* **Then** the `expiryDate` input is pre-filled with a value equal to **D + 3 months** (calendar months, same day-of-month or last day of month if shorter)

### AC-5 – Existing Poll Retrieval by UUID
* **Given** there exists a stored poll identified by UUID **U**
* **When** the visitor navigates to `/schedule-event-step3/{U}`
* **Then** the poll data is loaded from the configured `PollStorageService` implementation
* **And** all form fields are populated with the stored values, including `expiryDate`

### AC-6 – Missing or Unknown UUID Handling
* **Scenario A – missing UUID**
  * **Given** the visitor requests `/schedule-event-step3` (no path param)
  * **Then** they are redirected (HTTP 302) to `/schedule-event` and a flash warning message "Poll not found" is shown on the landing page
* **Scenario B – unknown UUID**
  * **Given** the visitor requests `/schedule-event-step3/{UNKNOWN}` where `UNKNOWN` is not present in storage
  * **Then** behaviour is identical to Scenario A

## Non-Functional Notes
* All controller methods must remain free of infrastructure specifics (Hexagonal)
* No production code is to be written until a failing JGiven test exists for the corresponding AC. 